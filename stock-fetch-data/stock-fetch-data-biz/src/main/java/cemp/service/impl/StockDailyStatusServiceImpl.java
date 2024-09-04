package cemp.service.impl;

import cemp.domain.response.ApiAllStockDetails;
import cemp.domain.response.ApiAllStockResponse;
import cemp.entity.StockAll;
import cemp.util.StockUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cemp.entity.StockDailyStatus;
import cemp.service.StockDailyStatusService;
import cemp.mapper.StockDailyStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
* @author xsb_t
* @description 针对表【stock_daily_status】的数据库操作Service实现
* @createDate 2024-09-03 15:12:00
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
        getBaseMapper().init();
    }

    @Override
    public List<StockDailyStatus> findUnhandle() {
        return getBaseMapper().findUnhandle();
    }
}




