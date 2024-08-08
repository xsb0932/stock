package cemp;

//import com.api.AlarmApi;
import com.api.AlarmApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@EnableDiscoveryClient
@ComponentScan("cemp.*")
@EnableFeignClients(clients = {AlarmApi.class
})
public class CempBmsBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempBmsBizApplication.class, args);
    }

}
