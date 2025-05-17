package com.virtue.app.controller;

import com.virtue.app.domain.Member;
import com.virtue.app.domain.Sales;
import com.virtue.app.dto.MemberDto;
import com.virtue.app.repository.MemberRepository;
import com.virtue.app.repository.SalesRepository;
import com.virtue.app.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final SalesRepository salesRepository;

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
    public String myPage(
            @RequestParam(defaultValue = "1") int page,
            Authentication auth, 
            Model model) {
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        // 페이징 처리 (최근 주문순)
        PageRequest pageRequest = PageRequest.of(page - 1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Sales> ordersPage = salesRepository.findByMember(member, pageRequest);
        
        // DTO로 변환
        Page<OrderDto> orderDtos = ordersPage.map(order -> new OrderDto(
            order.getId(),
            order.getTitle(),
            order.getPrice(),
            order.getCount(),
            order.getImgUrl(),
            order.getStatus(),
            order.getCreatedAt()
        ));
        
        model.addAttribute("orders", orderDtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderDtos.getTotalPages());
        
        return "mypage";
    }

    @DeleteMapping("/order/{orderId}")
    @ResponseBody
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId, Authentication auth) {
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        Sales order = salesRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        
        if (!order.getMember().getId().equals(member.getId())) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }
        
        salesRepository.deleteById(orderId);
        return ResponseEntity.ok("주문이 삭제되었습니다.");
    }

    @GetMapping("/user/1")
    @ResponseBody
    public MemberDto getUser() {
        var a = memberRepository.findById(1L);
        var result = a.get();
        var data = new MemberDto(result.getUsername(), result.getDisplayName());

        return data;
    }

    // DTO 클래스 추가
    @Getter
    @AllArgsConstructor
    public static class OrderDto {
        private Long id;
        private String title;
        private Integer price;
        private Integer count;
        private String imgUrl;
        private Sales.OrderStatus status;
        private LocalDateTime createdAt;
    }
}


