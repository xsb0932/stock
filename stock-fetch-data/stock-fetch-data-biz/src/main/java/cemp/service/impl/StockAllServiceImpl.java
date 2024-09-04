package cemp.service.impl;

import cemp.domain.response.ApiAllStockDetails;
import cemp.domain.response.ApiAllStockResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cemp.entity.StockAll;
import cemp.service.StockAllService;
import cemp.mapper.StockAllMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
}




