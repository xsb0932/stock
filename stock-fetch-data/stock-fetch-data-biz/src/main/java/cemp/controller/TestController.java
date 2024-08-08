package cemp.controller;


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

    @GetMapping("/list")
    public String list() {
        System.out.println("success");
        String result = api.list();
        return result;
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("test");
        return "test";
    }
}
