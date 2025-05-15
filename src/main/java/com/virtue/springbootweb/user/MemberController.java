package com.virtue.springbootweb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    @PostMapping("/member")
    public String addMember(String username, String password, String displayName) throws Exception {
        memberService.saveUser(username, password, displayName);
        return "redirect:/list";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/my-page")
    public String myPage(Authentication auth) {
        System.out.println(auth.getAuthorities().contains(
                new SimpleGrantedAuthority("일반유저")
        ));
        return "mypage.html";
    }
}
