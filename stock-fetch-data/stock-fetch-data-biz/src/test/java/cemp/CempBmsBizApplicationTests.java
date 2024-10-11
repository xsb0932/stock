package cemp;

import cemp.common.util.DateUtils;
import cemp.conf.StockMailSender;
import cemp.domain.response.ApiCurrentResponse;
import cemp.redis.util.RedisUtils;
import cemp.service.ArthasTestErrorService;
import cemp.service.FetchDataService;
import com.alibaba.fastjson2.JSON;
import com.google.common.cache.LoadingCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static cemp.common.constant.StockCommonConstant.*;

@SpringBootTest
class CempBmsBizApplicationTests {

//    @Autowired
//    JavaMailSender javaMailSender;
    @Autowired
    StockMailSender stockMailSender;

    @Autowired
    FetchDataService fetchDataService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    LoadingCache guavaHoliday;

    @Autowired
    ArthasTestErrorService arthasTestErrorService;

    @Autowired
    KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Autowired
    RestTemplate restTemplate;


    @Test
    void contextLoads() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://stockapi.com.cn/v1/base/second?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b&code=002648&all=1";
        String response = restTemplate.getForObject(url,String.class);
        kafkaTemplate.send("stock_current", response.getBytes());
    }

    @Test
    void timeTest() {
        String time = "2024-09-06T01:15:00Z";
        ZonedDateTime time2 = DateUtils.utc2BJDatetime(time);
        String time3 = DateUtils.utc2BJStr(time);
        System.out.println("success");
    }



    @Test
    void guavaTest() throws ExecutionException {
//        Set<Object> result =  redisUtils.sget("stock:holiday");
//        guavaHoliday.put("stock:holiday",result);
        Set<String> result2 = (Set<String>)guavaHoliday.get("stock:holiday");
        System.out.println(123);
    }

    @Test
    void test() {
        fetchDataService.test2();
    }

    @Test
    void maintainDaily(){
        fetchDataService.maintainDaily();
    }

    @Test
    void error1(){
        arthasTestErrorService.error1();
    }

    @Test
    void historySH(){
        fetchDataService.historySH();
    }

    @Test
    void maintainMonthly(){
        fetchDataService.maintainMonthly();
    }

    @Test
    void current(){
        fetchDataService.staCurrent();
    }

    @Test
    void kafkatest(){
        kafkaTemplate.send("test","test_message".getBytes());

    }

    @Test
    void kafkaHistorySend(){
        //查询
        String url = "https://stockapi.com.cn/v1/base2/secondHistory?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b&date=2024-09-30&code=688060";
        //log.info(url);
        ApiCurrentResponse response =  restTemplate.getForObject(url,ApiCurrentResponse.class);

        kafkaTemplate.send("test","test_message".getBytes());

    }


    @Test
    void isHoliday(){
        String key = "stock:holiday";
        String date = "2024-10-01";
        fetchDataService.isHoliday(key,date);
    }

    @Test
    void sendMail(){
        stockMailSender.send("xsb_terry@163.com","597780384@qq.com","test msg");
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("xsb_terry@163.com");
//        message.setCc("xsb_terry@163.com");
//        message.setTo("597780384@qq.com");
//        message.setText("test message");
//        javaMailSender.send(message);
        System.out.println("success");
    }

}
