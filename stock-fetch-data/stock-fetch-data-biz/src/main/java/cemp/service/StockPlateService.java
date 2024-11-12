package cemp.service;

import cemp.entity.StockPlate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xsb_t
* @description 针对表【stock_plate】的数据库操作Service
* @createDate 2024-11-06 00:57:08
*/
public interface StockPlateService extends IService<StockPlate> {


    public void doAdd();

    void init();
}
