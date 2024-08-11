package cemp.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiAllStockResponse {

    private String code;
    private String msg;
    private List<ApiAllStockDetails> data;

}
