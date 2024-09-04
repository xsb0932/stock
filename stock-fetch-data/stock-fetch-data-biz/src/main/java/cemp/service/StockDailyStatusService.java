package cemp.service;

import cemp.entity.StockDailyStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author xsb_t
* @description 针对表【stock_daily_status】的数据库操作Service
* @createDate 2024-09-04 16:37:02
*/
public interface StockDailyStatusService extends IService<StockDailyStatus> {

    void updateAllStocks();

    void init();


    List<StockDailyStatus> findUnhandle();
}
