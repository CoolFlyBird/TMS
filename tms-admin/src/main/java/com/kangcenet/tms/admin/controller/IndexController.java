package com.kangcenet.tms.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "";
    }
}
