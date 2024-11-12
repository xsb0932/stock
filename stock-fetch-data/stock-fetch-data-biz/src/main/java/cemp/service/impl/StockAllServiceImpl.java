package cemp.service.impl;

import cemp.domain.response.ApiAllStockDetails;
import cemp.domain.response.ApiAllStockResponse;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cemp.entity.StockAll;
import cemp.service.StockAllService;
import cemp.mapper.StockAllMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
* @author xsb_t
* @description 针对表【stock_all】的数据库操作Service实现
* @createDate 2024-09-04 18:55:32
*/
@Service
@RequiredArgsConstructor
public class StockAllServiceImpl extends ServiceImpl<StockAllMapper, StockAll>
    implements StockAllService{

    @Autowired
    RestTemplate restTemplate;
    @Override
    public void init() {
        //删除所有
        getBaseMapper().delete(null);
        //查询所有股票
        String url = "https://stockapi.com.cn/v1/base/all?token=77a665b92b6249728cb29a53e9cf0918560093a778c3da0b";
        ApiAllStockResponse response =  restTemplate.getForObject(url,ApiAllStockResponse.class);
        List<ApiAllStockDetails> details = response.getData();
        details = details.stream().map(stock -> {
            stock.setIssta("0");
            return stock;
        }).collect(Collectors.toList());
        // 添加
        details.forEach(new Consumer<ApiAllStockDetails>() {
            @Override
            public void accept(ApiAllStockDetails stockDetail) {
                StockAll stock = new StockAll(null,stockDetail.getApi_code(),stockDetail.getGl(),stockDetail.getName(),stockDetail.getJys());
                getBaseMapper().insert(stock);
            }
        });
    }

    @Override
    public List<StockAll> selectBatch50(int pageSize, int pageNum) {
        LambdaQueryWrapper<StockAll> lqw = new LambdaQueryWrapper<>();
        IPage<StockAll> page = new Page<>(pageNum, pageSize);
        IPage<StockAll> result = this.getBaseMapper().selectPage(page, lqw);
        List<StockAll> records = result.getRecords();
        return records;
    }

    @Override
    public Integer totalBatchNum() {
        Long totalNum = this.getBaseMapper().selectCount(null);
        return NumberUtil.div(new BigDecimal(totalNum), new BigDecimal(200),0, RoundingMode.FLOOR).intValue();
    }

    public static void main(String[] args) {
        Long totalNum = (520333L);
        System.out.println(NumberUtil.div(new BigDecimal(totalNum), new BigDecimal(200),0, RoundingMode.CEILING));
    }
}




