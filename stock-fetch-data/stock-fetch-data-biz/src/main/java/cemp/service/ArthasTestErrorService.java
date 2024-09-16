package cemp.service;

import cemp.conf.StockMailSender;
import cemp.config.InfluxDBUtils;
import cemp.domain.response.ApiAllStockDetails;
import cemp.domain.response.ApiAllStockResponse;
import cemp.domain.response.ApiCurrentDetails;
import cemp.domain.response.ApiCurrentResponse;
import cemp.entity.BusStaDate;
import cemp.entity.StockDailyStatus;
import cemp.mapper.BusStaDateMapper;
import cemp.mapper.StockAllMapper;
import cemp.mapper.StockDailyStatusMapper;
import cemp.redis.util.RedisUtils;
import cemp.util.DateUtil;
import cemp.util.DateUtils;
import cemp.util.StockUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cemp.common.constant.StockCommonConstant.*;
import static cemp.constant.RedisKey.STOCK_ALL_KEY;
import static cemp.constant.RedisKey.STOCK_CURRENT_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArthasTestErrorService {



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


    private final BusStaDateMapper busStaDateMapper;
    private final StockDailyStatusMapper stockDailyStatusMapper;
    private final StockAllMapper stockAllMapper;


    /**
     * 查询重点股票每天的开盘价
     * @return
     */
    public List<String> error1() {
        //String [] stocks = new String[]{"603586","605598","603819"};
        String stockCode = "600761";
        String [] stocks = new String[]{"002648","600019","600761"};
        LocalDate date = LocalDate.now().minusDays(10);
        List<String> result = Arrays.stream(stocks).map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return getBegin(s,date);
            }
        }).collect(Collectors.toList());
        return result;

    }

    private String getBegin(String stockCode, LocalDate date) {
        String measurement = "stock_"+ stockCode;
        String begin  =  String.format("%s-%s-%s %s:%s:%s",date.getYear(),String.format("%02d", date.getMonthValue()),String.format("%02d", date.getDayOfMonth()),"00","00","00");
        String end  =  String.format("%s-%s-%s %s:%s:%s",date.getYear(),String.format("%02d", date.getMonthValue()),String.format("%02d", date.getDayOfMonth()),"23","59","59");
        String sql = "select * from "+measurement+" where time > '"+begin+"' and time < '"+end+"' order by time limit 1";
        QueryResult result = influxDBUtils.query(sql);

        if(result.getResults() != null &&result.getResults().size()>0){
            QueryResult.Series s = result.getResults().get(0).getSeries().get(0);
            //列名
            List<String> fields = s.getColumns();
            Map<String, String> tags = s.getTags();
            if (!CollectionUtils.isEmpty(s.getValues())) {
                return s.getValues().get(0).get(3).toString();
            }
        }
        return null;
    }

    public void error2() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
