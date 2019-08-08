package com.zrobot.demo;

import com.zrobot.demo.service.DemoService;
import com.zrobot.formwork.annotation.Autowired;
import com.zrobot.formwork.annotation.Controller;
import com.zrobot.formwork.annotation.RequestMapping;
import com.zrobot.formwork.annotation.RequestParam;
import com.zrobot.formwork.webmvc.ModelAndView;

/**
 * @author weizhaoli
 * @title: DemoController
 * @projectName selfspring
 * @description: TODO
 * @date 2019/7/29 15:23
 */
@Controller
@RequestMapping("/demoController")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping("/add")
    public ModelAndView demo(@RequestParam("name") String name){
        String s = demoService.testDemo();
        System.out.println(s);

        return null;
    }
}
