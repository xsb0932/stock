package cemp.service;

import cemp.common.dto.StockCurrentSendDto;
import cemp.conf.StockMailSender;
import cemp.config.InfluxDBUtils;
import cemp.domain.response.*;
import cemp.entity.BusStaDate;
import cemp.entity.StockAll;
import cemp.entity.StockDailyStatus;
import cemp.mapper.BusStaDateMapper;
import cemp.mapper.StockAllMapper;
import cemp.mapper.StockDailyStatusMapper;
import cemp.redis.util.RedisUtils;
import cemp.util.DateUtil;
import cemp.util.DateUtils;
import cemp.util.StockHttpUtil;
import cemp.util.StockUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.api.AlarmApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.cache.LoadingCache;
import io.netty.util.concurrent.CompleteFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static cemp.redis.constant.RedisKey.STOCK_CURRENT_KEY;
import static cemp.redis.constant.RedisKey.STOCK_ALL_KEY;
import static cemp.redis.constant.RedisKey.STOCK_HOLIDY;
import static cemp.common.constant.StockCommonConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FetchDataService {


    @Value("${stock.mail.from}")
    private String stockFrom;
    @Value("${stock.mail.to}")
    private String stockTo;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    InfluxDB influxDB;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    InfluxDBUtils influxDBUtils;
    @Autowired
    StockMailSender stockMailSender;
    @Autowired
    StockSendMailService stockSendMailService;
    @Autowired
    StockAllService stockAllService;
    @Autowired
    StockDailyStatusService stockDailyStatusService;
    @Autowired
    StockPlateService stockPlateService;
    @Autowired
    StockKpiDayService stockKpiDayService;
    @Autowired
    AlarmApi alarmApi;
    @Autowired
    LoadingCache guavaHoliday;
    @Autowired
    ThreadPoolTaskExecutor baseStockExecutor;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    KafkaTemplate<byte[], byte[]> kafkaTemplate;


    private final BusStaDateMapper busStaDateMapper;
    private final StockDailyStatusMapper stockDailyStatusMapper;
    private final StockAllMapper stockAllMapper;

    public void test2(){
        int a = 1/0;
        log.info("123456");
    }

    private Integer getTaskNum(){
        Object taskNum = redisUtils.get("stock_base_history_task");
        if(taskNum == null){
            redisUtils.set("stock_base_history_task",1,120);
            return 1;
        }else{
            return Integer.valueOf(taskNum.toString());
        }

    }

    public void sendBaseHistoryBatch(String startDate,String endDate){
        //查询总批次
        Integer bachNum = 0;
        bachNum = stockAllService.totalBatchNum();
        //
        for (int i = 0; i < bachNum; i++) {
            sendBaseHistoryTask(startDate,endDate);
        }


    }


    public void sendBaseHistoryOneByOne(String startDate,String endDate){
        //查询所有股票
        List<String> allStocks = stockAllService.totalStocks();
        allStocks.forEach(new Consumer<String>() {
            @Override
            public void accept(String stock) {
                sendBaseHistoryOneStock(startDate,endDate,stock);
            }
        });
    }

    public void sendBaseHistoryOneStock(String startDate,String endDate,String stockCode){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "发送rmq任务";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> manMap = new HashMap<>();
        manMap.put("messageId", messageId);
        manMap.put("messageData", messageData);
        manMap.put("createTime", createTime);
        manMap.put("startDate",startDate);
        manMap.put("endDate",endDate);
        manMap.put("stockCode",stockCode);
        /**
         * exchange 发送消息指定交换机
         * routingkey 指定topic routingkey 消息会根据rk 跑去绑定的 queue
         * Object 消息本身
         */
        rabbitTemplate.convertAndSend("baseStockHistoryExchange", "rmq_key_base_history", manMap);
        //redisUtils.incr("stock_base_history_task",1);
        System.out.println("success");
    }

    public void sendBaseHistoryTask(String startDate,String endDate){
        Integer taskNum = this.getTaskNum();
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "发送rmq任务";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> manMap = new HashMap<>();
        manMap.put("messageId", messageId);
        manMap.put("messageData", messageData);
        manMap.put("createTime", createTime);
        manMap.put("taskNum",taskNum);
        manMap.put("startDate",startDate);
        manMap.put("endDate",endDate);
        /**
         * exchange 发送消息指定交换机
         * routingkey 指定topic routingkey 消息会根据rk 跑去绑定的 queue
         * Object 消息本身
         */
        rabbitTemplate.convertAndSend("baseStockHistoryExchange", "rmq_key_base_history", manMap);
        redisUtils.incr("stock_base_history_task",1);
        System.out.println("success");
    }

    public String max(String stockCode){
        String sql = "select max(price) as price from test_stock";
        QueryResult result = influxDBUtils.query(sql);
        return null;
    }

    public String first(String stockCode){
        String sql = "select * from test_stock where time > '2024-08-09T00:00:00Z' and time < '2024-08-10T00:00:00Z' order by time limit 10";
        QueryResult result = influxDBUtils.query(sql);
        return null;
    }

    public String end(String stockCode){
        String sql = "select * from test_stock where time > '2024-08-09T00:00:00Z' and time < '2024-08-10T00:00:00Z' order by time desc limit 10";
        QueryResult result = influxDBUtils.query(sql);
        return null;
    }

    public String inter(String stockCode){
        String sql = "select * from test_stock  where time > '2024-08-09T06:00:00Z' and time < '2024-08-10T07:00:00Z'";
        QueryResult result = influxDBUtils.query(sql);
        return null;
    }

    public String baseTestHistory1(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 设置连接超时时间（毫秒）
        factory.setReadTimeout(3000);    // 设置读取超时时间（毫秒）
        RestTemplate restTemplate = new RestTemplate(factory);
        System.out.println("test begin");
        ResponseEntity<ApiCommonResponse<ApiBaseDayResponse>> response = StockHttpUtil.doGet(restTemplate,
                String.format("%s%s?token=%s&code=%s&endDate=%s&startDate=%s&calculationCycle=100",STOCK_HOST,STOCK_URL_BASE_DAY,STOCK_TOKEN,"002648","2024-11-10","2024-11-01"),
                HttpMethod.GET,
                new ParameterizedTypeReference<ApiCommonResponse<ApiBaseDayResponse>>() {}
        );
        System.out.println("test end");
        return "success";
    }

    public String baseTestHistory2(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        System.out.println("test begin");
        ResponseEntity<ApiCommonResponse<ApiBaseDayResponse>> response = StockHttpUtil.doGet(restTemplate,
                String.format("%s%s?token=%s&code=%s&endDate=%s&startDate=%s&calculationCycle=100",STOCK_HOST,STOCK_URL_BASE_DAY,STOCK_TOKEN,"002648","2024-11-10","2024-11-01"),
                HttpMethod.GET,
                new ParameterizedTypeReference<ApiCommonResponse<ApiBaseDayResponse>>() {}
        );
        System.out.println("test end");
        return "success";
    }

    /**
     * 维护所有股票代码
     * @return
     */
    public String getAllStocks(){
        String url = "https://stockapi.com.cn/v1/base/all?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b";
        ApiAllStockResponse response =  restTemplate.getForObject(url,ApiAllStockResponse.class);
        List<ApiAllStockDetails> details = response.getData();
        details = details.stream().map(stock -> {
            stock.setIssta("0");
            return stock;
        }).collect(Collectors.toList());
        //details.forEach(stock -> redisUtils.hset(STOCK_ALL_KEY,stock.getApi_code(),stock));

        //逐条插入
//        details.forEach(new Consumer<ApiAllStockDetails>() {
//            @Override
//            public void accept(ApiAllStockDetails stock) {
//                redisUtils.hset(STOCK_ALL_KEY.concat(":").concat(stock.getApicode()),"apicode",stock.getApicode());
//                redisUtils.hset(STOCK_ALL_KEY.concat(":").concat(stock.getApicode()),"gl",stock.getGl());
//                redisUtils.hset(STOCK_ALL_KEY.concat(":").concat(stock.getApicode()),"issta",stock.getIssta());
//                redisUtils.hset(STOCK_ALL_KEY.concat(":").concat(stock.getApicode()),"jys",stock.getJys());
//                redisUtils.hset(STOCK_ALL_KEY.concat(":").concat(stock.getApicode()),"name",stock.getName());
//            }
//        });
        //批量插入
        details.forEach(new Consumer<ApiAllStockDetails>() {
            @Override
            public void accept(ApiAllStockDetails stock) {
                Map<String,Object> map = BeanUtil.beanToMap(stock);
                redisUtils.hmset(STOCK_ALL_KEY.concat(":").concat(stock.getApi_code()),map);
            }
        });
        return "success";
    }

    public String testDB(){
        BusStaDate busStaDate = busStaDateMapper.selectById(1L);
        return "success";
    }

    public String testDB2(){
        LambdaQueryWrapper<BusStaDate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusStaDate::getId,1);
        busStaDateMapper.selectOne(lqw);
        return "success";
    }

    public void makeKPI(String date){
        //查询所有stock
        String[] stockCodes = new String[]{"002648","600761","600019"};
        List<Object> result =  redisUtils.hmget(STOCK_ALL_KEY,Arrays.asList(stockCodes));

        //遍历stock 计算kpi
//        result.forEach(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                JSONObject obj = (JSONObject)o;
//                obj.get
//            }
//        });
        for (String stockCode : stockCodes) {
            String measument = String.format("%s_%s","stock",stockCode);
            //刨去集合竞价的数据 (09:15:00 - 09:30:00)
            String sql = "select * from " + measument + " where time >= '" + DateUtils.getUTC(date.concat(" 09:30:00"))+"'";
            QueryResult list = influxDBUtils.query(sql);
            List<JSONObject> seriesData = Lists.newArrayList();
            if(list.getResults() != null &&list.getResults().size()>0){
                QueryResult.Series s = list.getResults().get(0).getSeries().get(0);
                //列名
                List<String> fields = s.getColumns();
                Map<String, String> tags = s.getTags();


                if (!CollectionUtils.isEmpty(s.getValues())) {
                    //每一行是一个List<Object>
                    for (List<Object> value : s.getValues()) {
                        JSONObject temp = JSONUtil.createObj();
                        for (int i = 0; i < fields.size(); i++) {
                            temp.set(fields.get(i), value.get(i));
                        }
                        // modify by hebin， group by的时候，把tag直接塞到result中返回
                        if (!MapUtil.isEmpty(tags)) {
                            tags.forEach((k, v) -> {
                                temp.set(k, v);
                            });
                        }
                        seriesData.add(temp);
                    }
                }
            }
            //写入kpi
            Double max = seriesData.stream().max((o1, o2) -> o1.getDouble("price").compareTo(o2.getDouble("price"))).get().getDouble("price");
            Double min = seriesData.stream().min((o1, o2) -> o1.getDouble("price").compareTo(o2.getDouble("price"))).get().getDouble("price");
            Double begin = seriesData.get(0).getDouble("price");
            Double end = seriesData.get(seriesData.size()-1).getDouble("price");
            BigDecimal trunover = new BigDecimal(0);
            trunover = seriesData.stream().map(entries -> BigDecimal.valueOf(entries.getDouble("price")).multiply(BigDecimal.valueOf(entries.getDouble("shoushu")))).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            BusStaDate detail = new BusStaDate(
                    null,
                    BigDecimal.valueOf(begin),
                    BigDecimal.valueOf(end),
                    BigDecimal.valueOf(max),
                    BigDecimal.valueOf(min),
                    trunover,
                    DateUtil.Date2Str(date),
                    stockCode
            );
            busStaDateMapper.insert(detail);
        }
        System.out.println("write kpi success");
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.Date2Str("2024-08-21"));
    }



    /**
     * 保存历史数据
     * @param date
     * @return
     */
    public String history5(String date){
//        String url = "https://stockapi.com.cn/v1/base2/secondHistory?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b&date=2024-08-08&code=002648"
        DateTimeFormatter LC_DT_FMT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat LC_DT_FMT_TIME_S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //查询所有股票

        //todo 目前只统计3个股票 006248 600019 6006761
        String[] stockCodes = new String[]{"002648","600761","600019"};
        Map map =  redisUtils.hmget(STOCK_ALL_KEY);
        map.keySet().stream().filter(o -> Arrays.asList(stockCodes).contains(o)).forEach(stockCode -> {
            String url = String.format("%s%s?token=%s&date=%s&code=%s",STOCK_HOST,STOCK_URL_HISTORY,STOCK_TOKEN,date,stockCode);
            ApiCurrentResponse response =  restTemplate.getForObject(url,ApiCurrentResponse.class);
            //插入历史数据
            BatchPoints batchPoints = BatchPoints.database("stock")
                    .consistency(InfluxDB.ConsistencyLevel.ALL)
                    .build();
            response.getData().forEach(new Consumer<ApiCurrentDetails>() {
                @Override
                public void accept(ApiCurrentDetails detail) {
                    try{
                        /** 批量插入 **/
                        Point.Builder builder = Point.measurement(STOCK_TABLE_PREFIX.concat(stockCode.toString()));
                        //可指定时间戳
                        Date dt = LC_DT_FMT_TIME_S.parse(detail.getTime());
                        builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                        //tag属性只能存储String类型
                        builder.tag("code", stockCode.toString());
                        //设置field
                        builder.addField("price", new BigDecimal(detail.getPrice()));
                        builder.addField("shoushu", Integer.valueOf(detail.getShoushu()));
                        builder.addField("danshu", Integer.valueOf(detail.getDanShu()));
                        batchPoints.point(builder.build());
                        /* 单条插入
                        Point.Builder builder = Point.measurement(STOCK_TABLE_PREFIX.concat(stockCode.toString()));
                        //可指定时间戳
                        Date dt = LC_DT_FMT_TIME_S.parse(detail.getTime());
                        builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                        //tag属性只能存储String类型
                        builder.tag("code", stockCode.toString());
                        //设置field
                        builder.addField("price", new BigDecimal(detail.getPrice()));
                        builder.addField("shoushu", Integer.valueOf(detail.getShoushu()));
                        builder.addField("danshu", Integer.valueOf(detail.getDanShu()));
                        builder.addField("bsbz", Integer.valueOf(detail.getBsbz()));
                        influxDB.write(builder.build());
                        */
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });
            influxDB.write(batchPoints);
        });


        return "success";
    }



    private List<LocalDate> getDateBetween(LocalDate begin, LocalDate end){
        List<LocalDate> rtnList = new ArrayList<>();
        while(begin.compareTo(end) < 0){
            if(StockUtils.isNotWeekend(begin) && !isHoliday(STOCK_HOLIDY,DateUtil.localDate2Str(begin))){
                rtnList.add(begin);
            }
            begin = begin.plusDays(1);
        }
        return rtnList;
    }

    private List<LocalDate> getUnhandleDates(String stockCode, Date lstDate){
        LocalDate now = LocalDate.now();
        LocalDate ldLstDate = DateUtil.date2LocalDate(lstDate);
        // 计算需要统计的历史时间-天
        List<LocalDate> daysBetween =  getDateBetween(ldLstDate,now);

        daysBetween.forEach(date -> {
            //todo 处理历史数据业务
            historyHandle(stockCode,DateUtil.localDate2Str(date));
        });
        return daysBetween;
    }

    /**
     * 处理单条历史数据
     * @param stockCode
     * @param date
     */
    private void historyHandle(String stockCode,String date){
        //查询
        String url = String.format("%s%s?token=%s&date=%s&code=%s",STOCK_HOST,STOCK_URL_HISTORY,STOCK_TOKEN,date,stockCode);
        //log.info(url);
        ApiCurrentResponse response =  restTemplate.getForObject(url,ApiCurrentResponse.class);
        //解析
        BatchPoints batchPoints = BatchPoints.database("stock")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        if(response != null){
            response.getData().forEach(detail -> {
                try{
                    /** 批量插入 **/
                    Point.Builder builder = Point.measurement(STOCK_TABLE_PREFIX.concat(stockCode.toString()));
                    //可指定时间戳
                    Date dt = DateUtil.fmt_ds.parse(detail.getTime());
                    builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                    //tag属性只能存储String类型
                    builder.tag("code", stockCode.toString());
                    //设置field
                    builder.addField("price", new BigDecimal(detail.getPrice()));
                    builder.addField("shoushu", Integer.valueOf(detail.getShoushu()));
                    builder.addField("danshu", Integer.valueOf(detail.getDanShu()));
                    batchPoints.point(builder.build());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            });
        }else{
            log.info("stockCode:".concat(stockCode).concat("response null"));
        }
        //
        LambdaQueryWrapper<StockDailyStatus> lqw = new LambdaQueryWrapper();
        lqw.eq(StockDailyStatus::getStockCode,stockCode);
        StockDailyStatus stockUO = new StockDailyStatus();
        stockUO.setStatus("1");
        stockUO.setLastDate(DateUtil.localDate2Date(LocalDate.now()));
        stockDailyStatusMapper.update(stockUO,lqw);
        //入库
        influxDB.write(batchPoints);
    }

    /**
     * 处理上海 历史数据
     * @return
     */
    public String historySH(){
        //查询待处理的50条股票
        List<StockDailyStatus> stocks = stockDailyStatusService.findUnhandle();
        //遍历
        for (StockDailyStatus stock : stocks) {
            //判断当前股票有多少历史数据需要补
            Date lastDate = stock.getLastDate();
            List<LocalDate> unhandleDates =  getUnhandleDates(stock.getStockCode(),lastDate);
            unhandleDates.forEach(new Consumer<LocalDate>() {
                @Override
                public void accept(LocalDate date) {
                    historyHandle(stock.getStockCode(),DateUtil.localDate2Str(date));
                }
            });
        }
        return "success";
    }

    /**
     * 统计当天实时数据
     */
    public void staCurrent(){

        LocalDate date = LocalDate.now();
        String[] stockCodes = new String[]{"002648","600761","600019"};
        //String[] stockCodes = new String[]{"002648"};
        for (int i = 0; i < stockCodes.length; i++) {
            String stockCode = stockCodes[i];

            //获取时间游标
            LocalDateTime timeCursor = getCursor(stockCode);
            // 判断是否已收盘
            if(this.isClose()){
                //log.info("已收盘");
                System.out.println("已收盘");
            }else{
                String url = String.format("%s%s?token=%s&code=%s&all=1",STOCK_HOST,STOCK_URL_CURRENT,STOCK_TOKEN,stockCode);
                ApiCurrentResponse response =  restTemplate.getForObject(url,ApiCurrentResponse.class);
                if(response != null && response.getData() != null){
                    //过滤有效数据,增量数据
                    List<ApiCurrentDetails> datas = response.getData().stream().filter(detail -> {
                        String fullTime = DateUtil.getDatePrefix().concat(detail.getTime());
                        LocalDateTime dt = LocalDateTime.parse(fullTime,DateUtil.dtf_ds);
                        return dt.compareTo(timeCursor) > 0;
                    }).collect(Collectors.toList());
                    String newDateCursor = StockUtils.stockBatchInsert(influxDB,"stock",stockCode,timeCursor,datas);
                    //维护新的游标
                    if(StringUtils.isNotBlank(newDateCursor)){
                        redisUtils.set(STOCK_CURRENT_KEY.concat(stockCode),newDateCursor);
                    }
                    //异动通知
                    Integer exNumber = 0;
                    //维护在缓存
                    if("002648".equals(stockCode)){
                        exNumber = 5000;
                    }else if("600019".equals(stockCode)){
                        exNumber = 10000;
                    }else if("600761".equals(stockCode)){
                        exNumber = 5000;
                    }

                    Integer finalExNumber = exNumber;
                    List<ApiCurrentDetails> exceptinDatas = datas.stream().filter(detail -> new BigDecimal(detail.getShoushu()).compareTo(BigDecimal.valueOf(finalExNumber)) > 0).collect(Collectors.toList());
                    if (exceptinDatas != null && exceptinDatas.size() > 0){
                        //stockMailSender.send(stockFrom,stockTo,String.format("stockCode:%s有异动,成交量手:%s",stockCode,exceptinDatas.get(0).getShoushu()));
                        stockSendMailService.sendMail(stockFrom,stockTo,String.format("stockCode:%s有异动,成交量手:%s",stockCode,exceptinDatas.get(0).getShoushu()));
                        log.info(String.format("stockCode:%s有异动,成交量手:%s",stockCode,exceptinDatas.get(0).getShoushu()));
                    }

                    //send kafka 全量数据
                    StockCurrentSendDto sendDto = new StockCurrentSendDto("",stockCode,response.getData());
                    //kafkaTemplate.send("stock_current", JSON.toJSONString(response.getData()).getBytes());
                    kafkaTemplate.send("stock_current", JSON.toJSONString(sendDto).getBytes());
                }

            }


        }

    }

    /**
     * 判断时候当天已收盘
     * @param dt
     * @return
     */
    private boolean isClose(LocalDateTime dt){
        // todo 判断收盘的标志维护再本地guava
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closeTime = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),15,0,0);
        if(dt.compareTo(closeTime) == 0){
            return true;
        }
        return false;
    }

    /**
     * 判断时候当天已收盘
     *
     */
    private boolean isClose(){
        // todo 判断收盘的标志维护再本地guava
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),9,15,0);
        LocalDateTime end = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),15,15,0);
        if (now.compareTo(begin) > 0&& now.compareTo(end) < 0){
            return false;
        }
        return true;
    }

    private LocalDateTime getCursor(String stockCode){
        Object obj = redisUtils.get(STOCK_CURRENT_KEY.concat(stockCode));
        if(obj != null){
            String timeCursor = obj.toString();
            return LocalDateTime.parse(timeCursor,DateUtil.dtf_ds);
        }else{
            LocalDate now = LocalDate.now();
            return LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),00,00,00);
        }
    }


    public String getCurrent(String stockCode){
        DateTimeFormatter LC_DT_FMT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat LC_DT_FMT_TIME_S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //查询游标
        String lastChargeTime = (String)redisUtils.get(STOCK_CURRENT_KEY.concat(stockCode));
        LocalDateTime lastTime = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.now();
        if(StringUtils.isBlank(lastChargeTime)){
            lastTime = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),9,15,1);
        }else{
            lastTime = LocalDateTime.parse(lastChargeTime,LC_DT_FMT_TIME);
        }

        String url = "https://stockapi.com.cn/v1/base/second?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b&code=002648&all=1";
        //String response = restTemplate.getForObject(url,String.class);
        //return response;
        ApiCurrentResponse response =  restTemplate.getForObject(url,ApiCurrentResponse.class);

        String datePrefix = String.format("%s-%s-%s ",now.getYear(),String.format("%02d", now.getMonthValue()),String.format("%02d", now.minusDays(1).getDayOfMonth()));
        //最后一条数据游标, 需要用redis来维护index



        //插入最后一条
        LocalDateTime finalLastTime = lastTime;
        List<ApiCurrentDetails> filterList =  response.getData().stream().filter(new Predicate<ApiCurrentDetails>() {
            @Override
            public boolean test(ApiCurrentDetails current) {
                String fullTime = datePrefix.concat(current.getTime());
                LocalDateTime dt = LocalDateTime.parse(fullTime,LC_DT_FMT_TIME);
                return dt.compareTo(finalLastTime) > 0;
            }
        }).collect(Collectors.toList());
        String measurement = "test_stock";

        filterList.forEach(new Consumer<ApiCurrentDetails>() {
            @Override
            public void accept(ApiCurrentDetails current) {

                try {
                    //构建
                    Point.Builder builder = Point.measurement(measurement);
                    //可指定时间戳
                    String fullTime = datePrefix.concat(current.getTime());
                    Date dt = LC_DT_FMT_TIME_S.parse(fullTime);
                    builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                    //tag属性只能存储String类型
                    builder.tag("code", stockCode);
                    //设置field
                    builder.addField("price", new BigDecimal(current.getPrice()));
                    builder.addField("shoushu", Integer.valueOf(current.getShoushu()));
                    builder.addField("danshu", Integer.valueOf(current.getDanShu()));
                    builder.addField("bsbz", Integer.valueOf(current.getBsbz()));
                    influxDB.write(builder.build());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                System.out.println("influxTest");
            }
        });
        //维护游标
        String newLastChargeTime =datePrefix.concat(filterList.get(filterList.size()-1).getTime());
        redisUtils.set(STOCK_CURRENT_KEY.concat(stockCode),newLastChargeTime);

        return "success";
    }

    /**
     * 1. 维护redis缓存
     * 2. 维护本地guava缓存 （增加访问速度）
     * 3.
     */
    public void maintainDaily() {
        //更新数据库status表
        stockDailyStatusService.updateAllStocks();
        //同步板块
        stockPlateService.init();


    }

    public void initBaseDayKpi(String startDate, String endDate) throws InterruptedException {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(2);
        executor.setKeepAliveSeconds(10000);
        executor.setThreadNamePrefix("log-operate-thread");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();

        for (int i = 1; i <= 4; i++) {
            int finalI = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    List<StockAll> result = stockAllService.selectBatch50(50, finalI);
//                    System.out.println("===="+result.size());
//                    result.stream().map(StockAll::getStockCode).forEach(System.out::println);
                    Set<String> stocks = result.stream().map(StockAll::getStockCode).collect(Collectors.toSet());
                    if(finalI==2){
                        System.out.println(stocks.size());
                        System.out.println("====thread2====");
                    }
                    result.forEach(new Consumer<StockAll>() {
                        @Override
                        public void accept(StockAll stock) {
                            stocks.remove(stock.getStockCode());
                            stockKpiDayService.doImport(startDate,endDate,stock.getStockCode());
                        }
                    });
                    System.out.println("============sets left begin===============");
                    stocks.stream().forEach(System.out::println);
                    System.out.println("============sets left end===============");


                    System.out.println("success");
                }
            });
        }
//        for (int i = 1; i <= 4; i++) {
//            //查询所有stock 50一批
//            List<StockAll> result = stockAllService.selectBatch50(50, i);
//            //根据stock_code 维护daily kpi
//            result.forEach(new Consumer<StockAll>() {
//                @Override
//                public void accept(StockAll stock) {
//                    stockKpiDayService.doImport(startDate,endDate,stock.getStockCode());
//                }
//            });
//        }
        System.out.println("success");
        Thread.currentThread().join();              //如果不join 主线程技术了 子线程就丢失了
    }


    public Integer test3(String startDate,String endDate,int i){
        List<StockAll> result = stockAllService.selectBatch50(50, i);
        result.forEach(new Consumer<StockAll>() {
            @Override
            public void accept(StockAll stock) {
                stockKpiDayService.doImport(startDate,endDate,stock.getStockCode());
            }
        });
        return 1;
    }


    public void initBaseDayKpi2(String startDate, String endDate) throws InterruptedException {
//        test3(startDate,endDate,1);
//        test3(startDate,endDate,2);
//        test3(startDate,endDate,3);
//        test3(startDate,endDate,4);
        List<CompletableFuture<Integer>> threads = new ArrayList<>();
        CompletableFuture<Integer> thread1 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,1), baseStockExecutor);
        CompletableFuture<Integer> thread2 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,2), baseStockExecutor);
        CompletableFuture<Integer> thread3 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,3), baseStockExecutor);
        CompletableFuture<Integer> thread4 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,4), baseStockExecutor);
        CompletableFuture.allOf(threads.toArray(new CompletableFuture<?>[4]));
        CompletableFuture.allOf(thread1,thread2,thread3,thread4).join();
    }


    public void initBaseDayKpi3(String startDate, String endDate,Integer taskNum) throws InterruptedException {

        List<CompletableFuture<Integer>> threads = new ArrayList<>();
        CompletableFuture<Integer> thread1 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,4* (taskNum-1) + 1), baseStockExecutor);
//        CompletableFuture<Integer> thread2 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,4* (taskNum-1) + 2), baseStockExecutor);
//        CompletableFuture<Integer> thread3 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,4* (taskNum-1) + 3), baseStockExecutor);
//        CompletableFuture<Integer> thread4 = CompletableFuture.supplyAsync(() -> test3(startDate,endDate,4* (taskNum-1) + 4), baseStockExecutor);
        threads.add(thread1);
//        threads.add(thread2);
//        threads.add(thread3);
//        threads.add(thread4);
        CompletableFuture.allOf(threads.toArray(new CompletableFuture<?>[4])).join();
//        CompletableFuture.allOf(thread1,thread2,thread3,thread4).join();
    }

    public void maintainMonthly(){
        //初始化stock_all
        stockAllService.init();
        //初始化stock_status
        stockDailyStatusService.init();

    }

    public String feegnPrint() {
        return alarmApi.list();
    }

    public boolean isHoliday(String key,String date) {
        //直接从redis中匹配
        //Boolean isHoliday = redisUtils.sismember(key, date);
        //先从本地缓存中找
        try {
            Set<String> result = (Set<String>)guavaHoliday.get(key);
            if(result.contains(date)){
                return true;
            }
        } catch (ExecutionException e) {
            //do nothing
        }
        return false;
    }
}
