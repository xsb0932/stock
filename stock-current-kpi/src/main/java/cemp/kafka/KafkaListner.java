package cemp.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListner {

    @KafkaListener(topics = "test",groupId = "${spring.kafka.consumer-group}")
    public void  getMsg1(String msg){
        System.out.println(msg);
    }
}
