package cemp.service;

import cemp.config.InfluxDBUtils;
import cemp.domain.response.ApiAllStockDetails;
import cemp.domain.response.ApiAllStockResponse;
import cemp.domain.response.ApiCurrentDetails;
import cemp.domain.response.ApiCurrentResponse;
import cemp.entity.BusStaDate;
import cemp.mapper.BusStaDateMapper;
import cemp.redis.util.RedisUtils;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static cemp.constant.RedisKey.STOCK_CURRENT_KEY;
import static cemp.constant.RedisKey.STOCK_ALL_KEY;
import static cemp.common.constant.StockCommonConstant.*;

@Service
@RequiredArgsConstructor
public class FetchDataService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    InfluxDB influxDB;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    InfluxDBUtils influxDBUtils;

    private final BusStaDateMapper busStaDateMapper;

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

    public String getAllStocks(){
        String url = "https://stockapi.com.cn/v1/base/all?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b";
        ApiAllStockResponse response =  restTemplate.getForObject(url,ApiAllStockResponse.class);
        List<ApiAllStockDetails> details = response.getData();
        details.forEach(new Consumer<ApiAllStockDetails>() {
            @Override
            public void accept(ApiAllStockDetails stock) {
                redisUtils.hset(STOCK_ALL_KEY,stock.getApi_code(),stock);
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
}
