package cemp.rmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockRMQQueueConfig {

    @Autowired
    DirectExchange baseStockHistoryExchange;

    @Bean
    public Queue baseStockHistoryQueue() {
        return new Queue("baseStockHistoryQueue",true);
    }
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(baseStockHistoryQueue()).to(baseStockHistoryExchange).with("rmq_key_base_history");
    }

}
