package cemp.service.impl;

import cemp.common.util.DateUtils;
import cemp.domain.response.ApiBKResponse;
import cemp.domain.response.ApiBaseDayResponse;
import cemp.domain.response.ApiCommonResponse;
import cemp.entity.StockKpiDay;
import cemp.mapper.StockKpiDayMapper;
import cemp.service.StockAllService;
import cemp.service.StockKpiDayService;
import cemp.util.DateUtil;
import cemp.util.StockHttpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cemp.common.constant.StockCommonConstant.*;

/**
* @author xsb_t
* @description 针对表【stock_kpi_day】的数据库操作Service实现
* @createDate 2024-11-06 01:59:53
*/
@Service
public class StockKpiDayServiceImpl extends ServiceImpl<StockKpiDayMapper, StockKpiDay>
    implements StockKpiDayService{

//    @Autowired
//    RestTemplate restTemplate;



    @Override
    public void doImport(String beginDate, String endDate, String stockCode) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 设置连接超时时间（毫秒）
        factory.setReadTimeout(3000);    // 设置读取超时时间（毫秒）
        RestTemplate restTemplate = new RestTemplate(factory);
        //删除原始数据
        this.clearByStockCode(beginDate,endDate,stockCode);
        //rest query
        System.out.println("=====stockcode="+stockCode+"===begin");
        ResponseEntity<ApiCommonResponse<ApiBaseDayResponse>> response = StockHttpUtil.doGet(restTemplate,
                String.format("%s%s?token=%s&code=%s&endDate=%s&startDate=%s&calculationCycle=100",STOCK_HOST,STOCK_URL_BASE_DAY,STOCK_TOKEN,stockCode,endDate,beginDate),
                HttpMethod.GET,
                new ParameterizedTypeReference<ApiCommonResponse<ApiBaseDayResponse>>() {}
                );
        System.out.println("=====stockcode="+stockCode+"===end");
        //resonstruct
        List<StockKpiDay> inputs = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(response.getBody().getData())){
            inputs = response.getBody().getData().stream().map(new Function<ApiBaseDayResponse, StockKpiDay>() {
                @Override
                public StockKpiDay apply(ApiBaseDayResponse baseDay) {
                    StockKpiDay kpiDay = new StockKpiDay();
                    kpiDay.setStockCode(stockCode);
                    kpiDay.setAmount(new BigDecimal(baseDay.getAmount()));
                    kpiDay.setStaDate(DateUtil.str2Date(baseDay.getTime()));
                    kpiDay.setPriceBegin(new BigDecimal(baseDay.getOpen()));
                    kpiDay.setPriceEnd(new BigDecimal(baseDay.getClose()));
                    kpiDay.setTurnoverRatio(new BigDecimal(baseDay.getTurnoverRatio()));
                    kpiDay.setAmount(new BigDecimal(baseDay.getAmount()));
                    kpiDay.setPriceIncrease(new BigDecimal(baseDay.getChange()));
                    kpiDay.setPriceMax(new BigDecimal(baseDay.getHigh()));
                    kpiDay.setPriceMin(new BigDecimal(baseDay.getLow()));
                    kpiDay.setPriceIncreaseRate(new BigDecimal(baseDay.getChangeRatio()));
                    return kpiDay;
                }
            }).collect(Collectors.toList());

            //input db
            this.getBaseMapper().insertBatchSomeColumn(inputs);
        }
    }

    private void clearByStockCode(String beginDate, String endDate, String stockCode){
        LambdaQueryWrapper<StockKpiDay> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StockKpiDay::getStockCode,stockCode);
        lqw.ge(StockKpiDay::getStaDate, DateUtil.str2Date(beginDate,DateUtil.fmt_dd));
        lqw.le(StockKpiDay::getStaDate,DateUtil.str2Date(endDate,DateUtil.fmt_dd));
        this.getBaseMapper().delete(lqw);
    }
}




