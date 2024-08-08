package cemp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@EnableDiscoveryClient
@ComponentScan("cemp.*")
public class CempMonitorBizApplication {

    public static void main(String[] args) {
//        String osName = System.getProperty("os.name").toLowerCase();
//        if (osName.contains("win")) {
//            // 如果是Windows系统，则禁用Nacos服务注册
//            System.setProperty("spring.cloud.nacos.discovery.enabled", "false");
//        }
        SpringApplication.run(CempMonitorBizApplication.class, args);
    }

}
