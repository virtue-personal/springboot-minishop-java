package com.virtue.springbootweb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/member")
    public String addMember(String username, String password, String displayName) {
        try {
            memberService.saveUser(username, password, displayName);
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/register?error=true";
        }
    }

    @GetMapping("/login")
    public String login() {
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


class MemberDto {
    public String username;
    public String displayName;

    MemberDto(String a, String b) {
        this.username = a;
        this.displayName = b;
    }
}