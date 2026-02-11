package com.kombaos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardingController {

    @RequestMapping(value = {
            "/",
            "/{segment:^(?!api|actuator).*$}/{path:[^\\.]*}",
            "/{segment:^(?!api|actuator).*$}/**/{path:[^\\.]*}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
