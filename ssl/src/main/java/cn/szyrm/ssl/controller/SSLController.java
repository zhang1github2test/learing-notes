package cn.szyrm.ssl.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ssl")
public class SSLController {

    @RequestMapping("/hello")
    public String pingpang(String hello){
        return  hello;
    }
}
