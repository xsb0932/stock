package cemp.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAllStockDetails {
    private String api_code;
    private String jys;
    private String name;
    private String gl;
    private String issta ;

}
