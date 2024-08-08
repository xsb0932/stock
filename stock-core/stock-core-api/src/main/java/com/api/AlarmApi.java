package com.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@FeignClient(name = "cemp-monitor")
public interface AlarmApi {

    @GetMapping("/list")
    String list();

}
