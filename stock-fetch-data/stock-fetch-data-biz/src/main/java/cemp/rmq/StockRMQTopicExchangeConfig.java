package cemp.rmq;


import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class StockRMQTopicExchangeConfig {

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("stockBaseHistoryTopicExchange");
    }


}
