package cemp.service.impl;

import cemp.util.StockUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cemp.entity.StockDailyStatus;
import cemp.service.StockDailyStatusService;
import cemp.mapper.StockDailyStatusMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
* @author xsb_t
* @description 针对表【stock_daily_status】的数据库操作Service实现
* @createDate 2024-09-04 16:37:02
*/
@Service
public class StockDailyStatusServiceImpl extends ServiceImpl<StockDailyStatusMapper, StockDailyStatus>
    implements StockDailyStatusService{

    @Override
    public void updateAllStocks() {
        //更新所有的股票 日写入状态
        getBaseMapper().updateAll(Arrays.asList(StockUtils.stocks));


    }

    @Override
    public void init() {
        getBaseMapper().delete(null);
        getBaseMapper().init();
    }

    @Override
    public List<StockDailyStatus> findUnhandle() {
        return getBaseMapper().findUnhandle();
    }
}




