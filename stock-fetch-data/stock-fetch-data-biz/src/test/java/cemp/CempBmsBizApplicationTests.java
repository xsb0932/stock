package cemp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@SpringBootTest
class CempBmsBizApplicationTests {


//    @Resource
//    RestTemplate restTemplate;


    @Test
    void contextLoads() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://stockapi.com.cn/v1/base/second?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b&code=002648&all=1";
        String response = restTemplate.getForObject(url,String.class);
        System.out.println(response);
    }

}
