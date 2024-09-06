package cemp;

import cemp.conf.StockMailSender;
import cemp.service.FetchDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class CempBmsBizApplicationTests {

//    @Autowired
//    JavaMailSender javaMailSender;
    @Autowired
    StockMailSender stockMailSender;

    @Autowired
    FetchDataService fetchDataService;

    @Test
    void contextLoads() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://stockapi.com.cn/v1/base/second?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b&code=002648&all=1";
        String response = restTemplate.getForObject(url,String.class);
        System.out.println(response);
    }

    @Test
    void maintainDaily(){
        fetchDataService.maintainDaily();
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
