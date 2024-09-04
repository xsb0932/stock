package cemp.mapper;

import cemp.entity.StockDailyStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author xsb_t
* @description 针对表【stock_daily_status】的数据库操作Mapper
* @createDate 2024-09-03 15:12:00
* @Entity cemp.entity.StockDailyStatus
*/
public interface StockDailyStatusMapper extends BaseMapper<StockDailyStatus> {

    @Insert("insert into stock_daily_status (stock_code , status, last_date) select stock_code , '0' , CURRENT_DATE from stock_all")
    void init();

    void updateAll(@Param("stockCodes") List<String> stockCodes);

    @Select("select * from stock_daily_status where status = '0' limit 50")
    List<StockDailyStatus> findUnhandle();
}




