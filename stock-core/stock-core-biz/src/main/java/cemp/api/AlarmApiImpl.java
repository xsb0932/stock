package cemp.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;

import com.api.AlarmApi;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class AlarmApiImpl implements AlarmApi {

    @Override
    public String list() {
        return "show list";
    }
}
