package cemp.config;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class InfluxDBUtils {

    @Autowired
    private InfluxDB influxDB;
    @Value("${spring.influx.database}")
    private String database;

    /**
     * 插入单条数据写法1
     *
     * @param measurement
     */
    public void insertOne01(String measurement) {
        //构建
        Point.Builder builder = Point.measurement(measurement);
        //可指定时间戳
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        //tag属性只能存储String类型
        builder.tag("name", "zhanggang");
        //设置field
        builder.addField("filed", "fileValue");
        influxDB.write(builder.build());
    }

    /**
     * 插入单条数据写法2
     *
     * @param measurement
     */
    public void insertOne02(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        //构建
        Point.Builder builder = Point.measurement(measurement);
        //可指定时间戳
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        //tag属性只能存储String类型
        builder.tag(tags);
        //设置field
        builder.fields(fields);
        influxDB.write(builder.build());
    }

    /**
     * 插入单条数据
     * influxDB开启UDP功能, 默认端口:8089,默认数据库:udp,没提供代码传数据库功能接口
     * 使用UDP的原因
     * TCP数据传输慢，UDP数据传输快。
     * 网络带宽需求较小，而实时性要求高。
     * InfluxDB和服务器在同机房，发生数据丢包的可能性较小，即使真的发生丢包，对整个请求流量的收集影响也较小。
     *
     * @param measurement
     * @param tags
     * @param fields
     */
    public void insertUDPOne03(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        //构建
        Point.Builder builder = Point.measurement(measurement);
        //可指定时间戳
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        //tag属性只能存储String类型
        builder.tag(tags);
        //设置field
        builder.fields(fields);
        int udpPort = 8089;
        influxDB.write(udpPort, builder.build());
    }


    //批量插入1
    public void insertBatchByRecords() {
        List<String> lines = new ArrayList<String>();
        String measurement = "test-inset-one";
        for (int i = 0; i < 2; i++) {
            Point.Builder builder = Point.measurement(measurement);
            //tag属性只能存储String类型
            builder.tag("name", "zhanggang" + i);
            //设置field
            builder.addField("filed", "fileValue" + i);
            lines.add(builder.build().lineProtocol());
        }
        influxDB.write(lines);
    }


    //批量插入2
    public void insertBatchByPoints() {
        BatchPoints batchPoints = BatchPoints.database(database)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        String measurement = "test-inset-one";
        for (int i = 0; i < 2; i++) {
            Point.Builder builder = Point.measurement(measurement);
            //tag属性只能存储String类型
            builder.tag("name", "zhanggang" + i);
            //设置field
            builder.addField("filed", "fileValue" + i);
            batchPoints.point(builder.build());
        }
        influxDB.write(batchPoints);
    }

    /**
     * 查询，返回Map集合
     *
     * @param query 完整的查询语句
     * @return
     */
    public List<Map<String, Object>> fetchRecords(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        QueryResult queryResult = influxDB.query(new Query(query, database));
        queryResult.getResults().forEach(result -> {
            result.getSeries().forEach(serial -> {
                List<String> columns = serial.getColumns();
                int fieldSize = columns.size();
                serial.getValues().forEach(value -> {
                    Map<String, Object> obj = new HashMap<String, Object>();
                    for (int i = 0; i < fieldSize; i++) {
                        obj.put(columns.get(i), value.get(i));
                    }
                    results.add(obj);
                });
            });
        });
        return results;
    }

    /**
     * 批量写入数据
     *
     * @param database        数据库
     * @param retentionPolicy 保存策略
     * @param consistency     一致性
     * @param records         要保存的数据（调用BatchPoints.lineProtocol()可得到一条record）
     */
    public void batchInsert(final String database, final String retentionPolicy,
                            final InfluxDB.ConsistencyLevel consistency, final List<String> records) {
        influxDB.write(database, retentionPolicy, consistency, records);
    }

    /**
     * 查询
     *
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command) {
        return influxDB.query(new Query(command, database));
    }

    /**
     * 创建数据保留策略
     * 设置数据保存策略 defalut 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1 副本个数为1/ 结尾DEFAULT
     * 表示 设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "defalut", database, "30d", 1);
        this.query(command);
    }
}
