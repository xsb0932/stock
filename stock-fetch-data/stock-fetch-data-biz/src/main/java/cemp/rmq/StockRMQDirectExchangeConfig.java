package cemp.rmq;


import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockRMQDirectExchangeConfig {

    @Bean
    DirectExchange baseStockHistoryExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange("baseStockHistoryExchange",true,false);
    }
}
