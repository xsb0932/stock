package cemp;

//import com.api.AlarmApi;
import com.api.AlarmApi;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
@EnableDiscoveryClient
@ComponentScan("cemp.*")
@MapperScan("cemp.mapper")
@EnableFeignClients(clients = {AlarmApi.class
})
public class CempBmsBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(CempBmsBizApplication.class, args);
    }

}
