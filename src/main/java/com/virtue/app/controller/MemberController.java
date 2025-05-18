package com.virtue.app.controller;

import com.virtue.app.dto.OrderDto;
import com.virtue.app.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 폼 페이지 반환
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 회원가입 처리
     */
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

    /**
     * 로그인 페이지 반환
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 마이페이지 - 주문 목록 조회
     */
    @GetMapping("/mypage")
    public String myPage(
            @RequestParam(defaultValue = "1") int page,
            Authentication auth,
            Model model) {

        Page<OrderDto> orderDtos = memberService.getOrderListForMyPage(auth.getName(), page);
        model.addAttribute("orders", orderDtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderDtos.getTotalPages());
        return "mypage";
    }

    /**
     * 주문 삭제 요청 처리
     */
    @DeleteMapping("/order/{orderId}")
    @ResponseBody
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId, Authentication auth) {
        boolean deleted = memberService.deleteOrder(auth.getName(), orderId);
        if (deleted) {
            return ResponseEntity.ok("주문이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }
    }

}