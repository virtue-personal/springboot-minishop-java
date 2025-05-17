package com.virtue.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 메인 페이지를 처리하는 메소드
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("welcome", "환영합니다!");
        model.addAttribute("description", "Spring Boot + Thymeleaf");
        return "index";
    }

    /**
     * 에러 페이지를 처리하는 메소드
     */
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("message", "페이지를 찾을 수 없습니다.");
        return "error";
    }
} 