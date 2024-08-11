package cemp.config;

import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.Data;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
public class InfluxdbConfig {
    @Value("${spring.influx.url}")
    private String influxDBUrl;

    @Value("${spring.influx.username}")
    private String userName;

    @Value("${spring.influx.password}")
    private String password;
    @Value("${spring.influx.retention_policy}")
    private String retention_policy;

    @Value("${spring.influx.database}")
    private String database;

    @Bean
    public InfluxDB influxdb() {
        InfluxDB influxDB = null;
        if (StringUtils.isEmpty(userName)) {
            influxDB = InfluxDBFactory.connect(influxDBUrl);
        } else {
            influxDB = InfluxDBFactory.connect(influxDBUrl, userName, password);
        }
        try {

            /**
             * 异步插入：
             * enableBatch这里第一个是point的个数，第二个是时间，单位毫秒
             * point的个数和时间是联合使用的，如果满100条或者60 * 1000毫秒
             * 满足任何一个条件就会发送一次写的请求。
             */
//            influxDB.setDatabase(database).enableBatch(100, 1000 * 60, TimeUnit.MILLISECONDS);
            influxDB.setDatabase(database);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //设置默认策略
            this.retention_policy = retention_policy == null || "".equals(retention_policy) ? "autogen" : retention_policy;
            influxDB.setRetentionPolicy(retention_policy);
        }
        //设置日志输出级别
        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
        return influxDB;
    }
}
