package com.filemanager.fm.controller;

import com.filemanager.fm.model.Person;
import com.filemanager.fm.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RestController
@RequestMapping("/fileManager")
public class PersonController {
    @Autowired
    PersonService personService;

    // 设置访问路由值为路径
    @RequestMapping("/getData")
    public Object index(){
        // 顾名思义 实体和数据 同时返回页面模板和数据
        List<Person> list = personService.getAll();
        return list;
    }
}
