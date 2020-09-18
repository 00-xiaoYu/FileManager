package com.filemanager.fm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String index() {
        System.out.println("第一个springboot程序");
        return "Hello World ，啊热you";
    }
}
