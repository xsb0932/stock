package cemp.rmq;

import cemp.redis.util.RedisUtils;
import cemp.service.FetchDataService;
import cemp.service.StockKpiDayService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import static cemp.common.constant.RedisKeyConstant.QUEUE_DUP_KEY;
import static cemp.common.constant.RedisKeyConstant.QUEUE_ID_KEY;

@Component
@Slf4j
public class StockRMQBaseHistoryListener {

    @Autowired
    FetchDataService fetchDataService;
    @Autowired
    StockKpiDayService stockKpiDayService;
    @Autowired
    RedisUtils redisUtils;
//    @Value("${domain.id}")
//    private String domainId;

    private static String localIp = null;
    static
    {
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            localIp = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

//    @RabbitListener(queues = "baseStockHistoryQueue")
//    //@RabbitListener(queues = "${domain.id}")
//    public void ListerAck(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, ClassNotFoundException, InterruptedException {
//        //判定当前处理状态： 一个service只能同时处理一个task
//        //如果当前状态为busy 那么消息重新入列
//        //获取锁
//        String key = "baseStockHistory_".concat(localIp);
//        Object current_status = redisUtils.get(key);
//        boolean isBusy = current_status == null ? false : ("busy".equals(current_status)? true : false);
//        if(isBusy){
//            System.out.println("丢回队列 重新消费");
//            channel.basicReject(tag,true);      //丢回队列 重新消费
//        }else{
//            try {
//                redisUtils.set(key,"busy");
//                ByteArrayInputStream strem =new ByteArrayInputStream(message.getBody());
//                ObjectInputStream strem2 =new ObjectInputStream(strem);
//                Map data = (Map)strem2.readObject();
//                Integer taskNum = (Integer)data.get("taskNum");
//                log.info("=======baseStockHistoryQueue:" + taskNum);
//                fetchDataService.initBaseDayKpi3("2024-11-01","2024-11-10",taskNum);
//                //redisUtils.incr("stock_base_history_task",1);
//                log.info("=======baseStockHistoryQueue end");
//            }finally {
//                redisUtils.set(key,"free");//标记为空闲
//            }
//        }
//    }


    @RabbitListener(queues = "baseStockHistoryQueue")
    //@RabbitListener(queues = "${domain.id}")
    public void ListerAck(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, ClassNotFoundException, InterruptedException {
        ByteArrayInputStream strem =new ByteArrayInputStream(message.getBody());
        ObjectInputStream strem2 =new ObjectInputStream(strem);
        Map data = (Map)strem2.readObject();
        String  stockCode = (String)data.get("stockCode");
        String startDate = (String)data.get("startDate");
        String endDate = (String)data.get("endDate");
        stockKpiDayService.doImport(startDate,endDate,stockCode);
    }

}
