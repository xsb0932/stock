package cemp.controller;


import cemp.service.FetchDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/fetch/data")
public class FetchDataController {
    private static final Logger logger = LogManager.getLogger(FetchDataController.class);
    @Autowired
    FetchDataService fetchDataService;

    /*
    -------------------------------------定时任务都做接口-----------------------------------------
     */

    @GetMapping("/test2")
    public String test2() {
        logger.info("99999");
        fetchDataService.test2();
        return "success";
    }

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
