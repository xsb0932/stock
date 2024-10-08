package cemp.util;

import cemp.service.FetchDataService;
import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class ScheduleTask {

    @Autowired
    FetchDataService fetchDataService;

    //固定5S 执行一次
//    @Scheduled(fixedRate = 5000)
    public void scheduleTask(){
        System.out.println("test");
    }

    /**
     * 指标计算 统计当天kpi
     * 每天16:00
     */
    @Scheduled(cron = "0 0 16 1/1 * ?")
    public void kpi() {
        if(StockUtils.isOpenToday()){
            // LocalDate date = LocalDate.now();
            // String dateStr = String.format("%s-%s-%s",date.getYear(),String.format("%02d", date.getMonthValue()),String.format("%02d", date.getDayOfMonth()));
            fetchDataService.makeKPI(StringUtils.trim(DateUtil.getDatePrefix()));
        }
    }



    /**
     * 实时数据
     * 重点股票每分钟更新数据
     *
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void current() {
        log.info("schedule task staCurrent begin");
        if(StockUtils.isOpenToday()){       //后期改成guava 本地缓存维护 （每天只计算一次）
            fetchDataService.staCurrent();
        }
        log.info("schedule task staCurrent end");
    }

    /**
     * 每个月月初凌晨0点
     * 初始化stock 基础数据
     * 每
     */
    @Scheduled(cron = "0 0 0 1 1/1 ?")
    public void initMonthly() {
        log.info("schedule task initMonthly begin");
        fetchDataService.maintainMonthly();
        log.info("schedule task initMonthly begin");
    }

    /**
     * 每天更新stock_status
     * 每天0 点
     *
     */
    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void initDaily() {
        log.info("schedule task initDaily begin");
        fetchDataService.maintainDaily();
        log.info("schedule task initDaily begin");
    }



//    /**
//     * 每天 8点执行
//     * 历史成交
//     * 先查看昨天是否已经有数据，如果没有数据 则补入历史数据
//     */
//    @Scheduled(cron = "0 0 8 1/1 * ?")
//    public void history() {
//        if(StockUtils.isOpenYestoday()){
//            LocalDate date = LocalDate.now().minusDays(1L);
//            String dateStr = String.format("%s-%s-%s",date.getYear(),String.format("%02d", date.getMonthValue()),String.format("%02d", date.getDayOfMonth()));
//            fetchDataService.history5(dateStr);
//        }
//    }


    /**
     * 每天1-8点执行,每5分钟执行一次,每个批次跑50条数据
     * 上海股票 前一天的历史数据入库
     *
     */
    @Scheduled(cron = "0 0/5 1,2,3,4,5,6,7,8 * * ?")
    public void historySH() {
        log.info("schedule task historySH begin");
        if(StockUtils.isOpenYestoday()){
            fetchDataService.historySH();
        }
        log.info("schedule task historySH begin");
    }

    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date.getDayOfWeek().getValue());
    }

}
