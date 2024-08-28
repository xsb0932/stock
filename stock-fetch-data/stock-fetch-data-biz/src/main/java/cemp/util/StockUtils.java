package cemp.util;

import cemp.domain.response.ApiCurrentDetails;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cemp.common.constant.StockCommonConstant.STOCK_TABLE_PREFIX;

@Slf4j
public class StockUtils {

    public static boolean isOpenYestoday(){
        LocalDate date = LocalDate.now().minusDays(1L);
        return date.getDayOfWeek().getValue() <6;
    }

    public static boolean isOpenToday(){
        LocalDate date = LocalDate.now();
        return date.getDayOfWeek().getValue() <6;
    }

    /**
     *
     * @param influxDB
     * @param database
     * @param stockCode
     * @param timeCursor 写入的最后一条数据 时间
     * @param datas 写入的stock数据（已过滤）
     * @return
     */
    public static String stockBatchInsert(InfluxDB influxDB, String database , String stockCode ,LocalDateTime timeCursor, List<ApiCurrentDetails> datas){
        BatchPoints batchPoints = BatchPoints.database("stock")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();


        //Long lastData = (Long)datas.get(datas.size()-1).get("time");  //batchinsert 不维护游标
        if(datas != null && datas.size() >0){

            datas.forEach(new Consumer<ApiCurrentDetails>() {
                @SneakyThrows
                @Override
                public void accept(ApiCurrentDetails detail) {
                    //可指定时间戳
                    //todo 生成的时间是否有问题
                    String dp = DateUtil.getDatePrefix().concat(detail.getTime());
                    log.info("time str:"+dp);
                    Date dt = DateUtil.fmt_ds.parse(dp);
                    log.info("time1:" + dt);
                    log.info("time2:"+ dt.getTime());
                    //Date dt = DateUtil.fmt_ds.parse(DateUtil.getDatePrefix().concat(detail.getTime()));
                    /** 批量插入 **/
                    Point.Builder builder = Point.measurement(STOCK_TABLE_PREFIX.concat(stockCode.toString()));
                    builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                    //tag属性只能存储String类型
                    builder.tag("code", stockCode.toString());
                    //设置field
                    builder.addField("price", new BigDecimal(detail.getPrice()));
                    builder.addField("shoushu", Integer.valueOf(detail.getShoushu()));
                    builder.addField("danshu", Integer.valueOf(detail.getDanShu()));
                    batchPoints.point(builder.build());
                }
            });
            influxDB.write(batchPoints);
            return DateUtil.getDatePrefix().concat(datas.get(datas.size()-1).getTime());
        }else{
            return null;
        }
    }

}
