package com.kombaos.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardingController {

    @RequestMapping(value = {
            "/",
            "/{path:(?!api|actuator|assets)[^\\.]*}",
            "/{path:(?!api|actuator|assets)[^\\.]*}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
