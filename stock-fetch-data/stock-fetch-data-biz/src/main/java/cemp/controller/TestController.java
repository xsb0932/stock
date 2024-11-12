package cemp.controller;


import cemp.service.FetchDataService;
import com.api.AlarmApi;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final AlarmApi api;
    private final FetchDataService fetchDataService;
    @GetMapping("/list")
    public String list() {
        System.out.println("success");
        String result = api.list();
        return result;
    }

    @GetMapping("/test/base/stock")
    public String testBaseStock() throws InterruptedException {
        fetchDataService.initBaseDayKpi2("2024-11-01","2024-11-08");
        return "success";
    }

    @GetMapping("/test/base/history")
    public String testBaseHisotry() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            fetchDataService.sendBaseHistoryTask("2024-11-01","2024-11-10");
        }
        return "success";
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("test");
        return "test";
    }
}
