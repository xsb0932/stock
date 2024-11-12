package cemp.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


public class StockHttpUtil {

    public static ResponseEntity doGet(RestTemplate restTemplate,String url, HttpMethod method, ParameterizedTypeReference type){
        return restTemplate.exchange(url, HttpMethod.GET,null,type);
    }
}
