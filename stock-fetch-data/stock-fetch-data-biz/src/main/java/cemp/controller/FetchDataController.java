package cemp.controller;


import cemp.service.ArthasTestErrorService;
import cemp.service.FetchDataService;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    ArthasTestErrorService arthasTestErrorService;

    /*
    -------------------------------------定时任务都做接口-----------------------------------------
     */

    @GetMapping("/feign/print")
    public String feegnPrint() {
        fetchDataService.feegnPrint();
        return "success";
    }

    @GetMapping("/test2")
    public String test2(HttpServletRequest req) {
        logger.info("99999");
        if(req.getHeader("X-Tenant-Id") ==null){
            throw new RuntimeException("没有传入租户信息，接口请求终止");
        }
        return "success";
    }

    @GetMapping("/current")
    public String current() {
        fetchDataService.staCurrent();
        return "success";
    }

    @GetMapping("/base/history/batch")
    public String baseHistoryBatch(@RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate) {
        fetchDataService.sendBaseHistoryBatch(startDate,endDate);
        return "success";
    }

    @GetMapping("/base/history/stocks")
    public String baseHistoryOneByOne(@RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate) {
        fetchDataService.sendBaseHistoryOneByOne(startDate,endDate);
        return "success";
    }

    @GetMapping("/base/all/stocks")
    public String baseHistory() {
        fetchDataService.getAllStocks();
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

    @GetMapping("/arthas/error1")
    public String error1() {
        arthasTestErrorService.error1();
        return "success";
    }

    @GetMapping("/arthas/error2")
    public String error2() {
        arthasTestErrorService.error2();
        return "success";
    }




}
