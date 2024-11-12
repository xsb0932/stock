package cemp.domain.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiBaseDayResponse implements Serializable {
    private String code;
    private String time;
    private String open;
    private String turnoverRatio;
    private String amount;
    private String change;
    private String high;
    private String low;
    private String changeRatio;
    private String close;
    private String volume;

}
