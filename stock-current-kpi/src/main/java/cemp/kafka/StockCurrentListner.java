package cemp.kafka;

import cemp.common.util.DateUtils;
import cemp.config.InfluxDBUtils;
import cemp.redis.util.RedisUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import static cemp.redis.constant.RedisKey.STOCK_CURRENT_STATUS;

@Component
public class StockCurrentListner {


    @Autowired
    InfluxDBUtils influxDBUtils;
    @Autowired
    RedisUtils redisUtils;

    /**
     * {
     * shoushu:"889"
     * price:"29.55"
     * bsbz:"4"
     * time:"09:16:00"
     * }
     * @param msg
     */
    @KafkaListener( topics = "stock_current",groupId = "${spring.kafka.consumer-group}")
//    @KafkaListener(topics = "stock_current",groupId = "${spring.kafka.consumer-group}", topicPartitions={
//            @TopicPartition(topic = "stock_current",
//                    partitionOffsets = {
//                            @PartitionOffset(partition = "0", initialOffset = "15"),
//                    }
//            )
//    })
    public void  current_api(String msg){

        System.out.println(msg);
        JSONObject obj = JSON.parseObject(msg);

        String stockCode = obj.getString("code");
        String datas = obj.getString("data");
        JSONArray jDatas = JSON.parseArray(datas);
        JSONObject kpi = new JSONObject();
        kpi.put("begin",((JSONObject)jDatas.get(0)).getDoubleValue("price"));
        JSONObject lastObj = (JSONObject)jDatas.get(jDatas.size()-1);
        kpi.put("current",lastObj.getDoubleValue("price"));
        if("15:00:00".equals(lastObj.getString("time"))){
            kpi.put("end",lastObj.getDoubleValue("price"));
        }

        kpi.put("max",jDatas.stream().map(o -> Double.valueOf(((JSONObject)o).getString("price"))).max((o1, o2) -> NumberUtil.compare(o1,o2)).get());
        kpi.put("min",jDatas.stream().map(o -> Double.valueOf(((JSONObject)o).getString("price"))).min((o1, o2) -> NumberUtil.compare(o1,o2)).get());

        Map<String,Object> map = BeanUtil.beanToMap(kpi);
        //send to redis
        redisUtils.hmset(STOCK_CURRENT_STATUS.concat(":").concat(stockCode),map);



//        String date = "2023-09-26";     //todo
//
//        //查询实时
//        String measument = String.format("%s_%s","stock",stockCode);
//        String sql = "select * from " + measument + " where time >= '" + DateUtils.getUTC(date.concat(" 09:30:00"))+"'";
//        QueryResult list = influxDBUtils.query(sql);
//        List<cn.hutool.json.JSONObject> seriesData = Lists.newArrayList();
//        if(list.getResults() != null &&list.getResults().size()>0){
//            QueryResult.Series s = list.getResults().get(0).getSeries().get(0);
//            //列名
//            List<String> fields = s.getColumns();
//            Map<String, String> tags = s.getTags();
//
//
//            if (!CollectionUtils.isEmpty(s.getValues())) {
//                //每一行是一个List<Object>
//                for (List<Object> value : s.getValues()) {
//                    cn.hutool.json.JSONObject temp = JSONUtil.createObj();
//                    for (int i = 0; i < fields.size(); i++) {
//                        temp.set(fields.get(i), value.get(i));
//                    }
//                    // modify by hebin， group by的时候，把tag直接塞到result中返回
//                    if (!MapUtil.isEmpty(tags)) {
//                        tags.forEach((k, v) -> {
//                            temp.set(k, v);
//                        });
//                    }
//                    seriesData.add(temp);
//                }
//            }
//        }
//        System.out.println("123");
//        //统计指标
//        JSONObject kpi = new JSONObject();
//        kpi.put("max",seriesData.stream().max(new Comparator<cn.hutool.json.JSONObject>() {
//            @Override
//            public int compare(cn.hutool.json.JSONObject o1, cn.hutool.json.JSONObject o2) {
//                return o1.getDouble("price").compareTo(o2.getDouble("price"));
//            }
//        }));
//        kpi.put("min",seriesData.stream().max(new Comparator<cn.hutool.json.JSONObject>() {
//            @Override
//            public int compare(cn.hutool.json.JSONObject o1, cn.hutool.json.JSONObject o2) {
//                return o2.getDouble("price").compareTo(o1.getDouble("price"));
//            }
//        }));
//        kpi.put("begin",seriesData.stream().filter(new Predicate<cn.hutool.json.JSONObject>() {
//            @Override
//            public boolean test(cn.hutool.json.JSONObject obj) {
//                String time = obj.getStr("time");
//                String openTime = time.substring(0,10).concat(" 15:00:00");
//                return DateUtils.time2ZDT(time,DateUtils.FDT_PATTERN_UTC).compareTo(DateUtils.time2ZDT(openTime,DateUtils.FDT_PATTERN_UTC)) > 0;
//            }
//        }).findFirst().get().getDouble("price"));
//        kpi.put("end",seriesData.get(seriesData.size()).getDouble("price"));
//        Map<String,Object> map = BeanUtil.beanToMap(kpi);
//        //send to redis
//        redisUtils.hmset(STOCK_ALL_KEY.concat(":").concat(stockCode),map);

    }
}
