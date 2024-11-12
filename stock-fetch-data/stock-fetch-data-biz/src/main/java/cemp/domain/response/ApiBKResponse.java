package cemp.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiBKResponse implements Serializable {
    private String plateCode;
    private String name;
    private String flag;

}
