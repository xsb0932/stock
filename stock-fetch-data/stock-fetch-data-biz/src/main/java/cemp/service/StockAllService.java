package cemp.service;

import cemp.entity.StockAll;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

/**
* @author xsb_t
* @description 针对表【stock_all】的数据库操作Service
* @createDate 2024-09-03 15:21:18
*/
public interface StockAllService extends IService<StockAll> {
    void init();
}
