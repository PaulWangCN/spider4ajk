package com.wangjx.spider4ajk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName IndexController.java
 * @Description 主页控制器
 * @createTime 2019年06月21日 15:48:00
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping("")
    public String home() {
        return "index";
    }

}
