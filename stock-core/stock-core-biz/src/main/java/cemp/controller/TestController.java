package cemp.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Scanner;


@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping("/list")
    public String list() {
        System.out.println("success");
        return "success";
    }

    @GetMapping("/gateway/test")
    public String gatewayTest(@RequestHeader(value = "token",required = false)String tk,  HttpServletRequest request) {
        String token = request.getHeader("token");
        System.out.println(tk);
        System.out.println(token);
        System.out.println("success");
        return "success";
    }


}
