package com.virtue.app.controller;

import com.virtue.app.domain.CustomUser;
import com.virtue.app.dto.MemberDto;
import com.virtue.app.repository.MemberRepository;
import com.virtue.app.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String register(String username, String password, String displayName, Model model) {
        try {
            memberService.saveUser(username, password, displayName);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("displayName", displayName);
            return "register";
        }
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "exception", required = false) String exception,
            Model model) {
        return "login";
    }

    @GetMapping("/mypage")
    public String myPage(Authentication auth) {
        CustomUser result = (CustomUser) auth.getPrincipal();
        return "mypage";
    }

    @GetMapping("/user/1")
    @ResponseBody
    public MemberDto getUser() {
        var a = memberRepository.findById(1L);
        var result = a.get();
        var data = new MemberDto(result.getUsername(), result.getDisplayName());

        return data;
    }
}


