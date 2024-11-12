package cemp.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class ApiCommonResponse<T> extends ApiResponseHead {

    private List<T> data;

}
