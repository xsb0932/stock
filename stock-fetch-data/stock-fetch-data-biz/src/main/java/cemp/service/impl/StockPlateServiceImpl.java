package cemp.service.impl;

import cemp.domain.response.ApiBKResponse;
import cemp.domain.response.ApiCommonResponse;
import cemp.domain.response.ApiCurrentResponse;
import cemp.entity.StockPlate;
import cemp.mapper.StockPlateMapper;
import cemp.service.StockPlateService;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cemp.common.constant.StockCommonConstant.*;

/**
* @author xsb_t
* @description 针对表【stock_plate】的数据库操作Service实现
* @createDate 2024-11-06 00:57:08
*/
@Service
public class StockPlateServiceImpl extends ServiceImpl<StockPlateMapper, StockPlate>
    implements StockPlateService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void doAdd() {
        StockPlate plate = new StockPlate();
        plate.setPlatecode("9999");
        plate.setFlag("1");
        plate.setName("test");
        this.getBaseMapper().insert(plate);
    }

    @Override
    public void init() {
        String urlBK = String.format("%s%s?token=%s",STOCK_HOST,STOCK_URL_BK,STOCK_TOKEN);
        String urlGN = String.format("%s%s?token=%s",STOCK_HOST,STOCK_URL_GN,STOCK_TOKEN);
        //getForObject转泛型 得到了 List<LinkedHashMap>  没有转换成 List<ApiBKResponse> 导致强转异常
        ParameterizedTypeReference<ApiCommonResponse<ApiBKResponse>> responseBodyType = new ParameterizedTypeReference<ApiCommonResponse<ApiBKResponse>>() {};
//        ApiCommonResponse<ApiBKResponse> response = restTemplate.getForObject(url,ApiCommonResponse.class);
        List<String > list = new ArrayList<>();
        ResponseEntity<ApiCommonResponse<ApiBKResponse>> entityBK = restTemplate.exchange(urlBK, HttpMethod.GET,null,responseBodyType);
        ResponseEntity<ApiCommonResponse<ApiBKResponse>> entityGN = restTemplate.exchange(urlGN, HttpMethod.GET,null,responseBodyType);
        //ApiCommonResponse<ApiBKResponse> response = restTemplate.exchange(url, HttpMethod.GET,null,responseBodyType,list);
        //ApiCommonResponse<ApiBKResponse> response = restTemplate.exch
        //List<ApiBKResponse> results =  response.getData();
        List<ApiBKResponse> results = entityBK.getBody().getData();
        List<ApiBKResponse> resultsGN = entityBK.getBody().getData();
        results = results.stream().map(apiBKResponse -> {
            apiBKResponse.setFlag("1");
            return apiBKResponse;
        }).collect(Collectors.toList());
        results.addAll(resultsGN.stream().map(apiBKResponse -> {
            apiBKResponse.setFlag("1");
            return apiBKResponse;
        }).collect(Collectors.toList()));

        StockPlateMapper mapper = this.getBaseMapper();
        mapper.delete(null);            //清空板块
        //入库
        results.forEach(new Consumer<ApiBKResponse>() {
            @Override
            public void accept(ApiBKResponse apiBKResponse) {
                StockPlate plate = new StockPlate(apiBKResponse.getPlateCode(),apiBKResponse.getName(),"1");
//                LambdaQueryWrapper<StockPlate> qw = new LambdaQueryWrapper<>();
//                qw.eq(StockPlate::getPlatecode,apiBKResponse.getPlateCode());
//                mapper.update(plate,qw);
                mapper.insert(plate);
            }
        });

    }
}




