package cemp.service;

import cemp.entity.StockAll;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author xsb_t
* @description 针对表【stock_all】的数据库操作Service
* @createDate 2024-09-04 18:55:32
*/
public interface StockAllService extends IService<StockAll> {
    void init();

    List<StockAll> selectBatch50(int pageSize, int pageNum);

    Integer totalBatchNum();

    List<String> totalStocks();
}
