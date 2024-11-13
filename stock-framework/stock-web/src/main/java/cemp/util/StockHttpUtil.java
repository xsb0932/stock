package cemp.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


public class StockHttpUtil {

    /**
     * 使用 restTemplate 的exchange方法可以解决 对象中的泛型的序列化和反序列化问题
     * @param restTemplate
     * @param url
     * @param method
     * @param type
     * @return
     */
    public static ResponseEntity doGet(RestTemplate restTemplate,String url, HttpMethod method, ParameterizedTypeReference type){
        return restTemplate.exchange(url, HttpMethod.GET,null,type);
    }
}
