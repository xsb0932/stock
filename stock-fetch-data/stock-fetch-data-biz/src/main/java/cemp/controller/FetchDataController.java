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

    @GetMapping("/current")
    public String list(@RequestParam("stockCode") String stockCode) {
        return fetchDataService.getCurrent(stockCode);

    }

    @GetMapping("/all")
    public String all() {
        return fetchDataService.getAllStocks();

    }
    @GetMapping("/testdb")
    public String testdb() {
        return fetchDataService.testDB();

    }

    @GetMapping("/max")
    public String max(@RequestParam("stockCode") String stockCode) {

        return fetchDataService.inter(stockCode);

    }

    @GetMapping("/history")
    public String history(@RequestParam("date") String date) {

        return fetchDataService.history5(date);

    }

}
