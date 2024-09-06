package cemp.controller;


import cemp.service.FetchDataService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


@RestController
@AllArgsConstructor
@RequestMapping("/fetch/data")
public class FetchDataController {

    @Autowired
    FetchDataService fetchDataService;

    /*
    -------------------------------------定时任务都做接口-----------------------------------------
     */

    @GetMapping("/current")
    public String current() {
        fetchDataService.staCurrent();
        return "success";
    }

    @GetMapping("/monthly")
    public String monthly() {
        fetchDataService.maintainMonthly();
        return "success";
    }

    @GetMapping("/daily")
    public String daily() {
        fetchDataService.maintainDaily();
        return "success";
    }

    @GetMapping("/history")
    public String history() {
        fetchDataService.historySH();
        return "success";
    }

    


}
