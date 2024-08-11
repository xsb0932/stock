package cemp.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCurrentDetails {
    private String time;
    private String price;
    private String shoushu;
    private String danShu;
    private String bsbz;
}
