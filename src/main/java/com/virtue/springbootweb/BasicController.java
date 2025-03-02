package com.virtue.springbootweb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class BasicController {
    @GetMapping("/")
    String hello() {
        return "index.html";
    }

    @GetMapping("/about")
    @ResponseBody
    String about() {
        return "피싱사이트에요";
    }

    @GetMapping("/date")
    @ResponseBody
    String date() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-DD hh:mm;ss"));
    }
}
