package com.virtue.app.controller;

import com.virtue.app.domain.Item;
import com.virtue.app.domain.Member;
import com.virtue.app.domain.Sales;
import com.virtue.app.repository.ItemRepository;
import com.virtue.app.repository.MemberRepository;
import com.virtue.app.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @GetMapping("/order")
    public String orderForm(@RequestParam Long itemId, Model model) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        model.addAttribute("item", item);
        return "order";
    }

    @PostMapping("/order")
    public String createOrder(
            @RequestParam Long itemId,
            @RequestParam Integer count,
            Authentication auth) {
            
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        Sales order = salesService.createOrder(itemId, count, member.getId());
        
        return "redirect:/order/complete/" + order.getId();
    }

    @GetMapping("/order/complete/{orderId}")
    public String orderComplete(@PathVariable Long orderId, Model model) {
        Sales order = salesService.getOrder(orderId);
        model.addAttribute("order", order);
        return "order-complete";
    }

    @PostMapping("/order/{orderId}/complete")
    @ResponseBody
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId, Authentication auth) {
        try {
            // 현재 로그인한 사용자 확인
            Member member = memberRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
            
            // 주문 조회
            Sales order = salesService.getOrder(orderId);
            
            // 주문 소유자 확인
            if (!order.getMember().getId().equals(member.getId())) {
                return ResponseEntity.status(403).body("권한이 없습니다.");
            }
            
            // 주문 상태 변경
            salesService.updateOrderStatus(orderId, Sales.OrderStatus.COMPLETED);
            
            return ResponseEntity.ok("입금확인이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/sales/order/{orderId}")
    @ResponseBody
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId, Authentication auth) {
        try {
            // 현재 로그인한 사용자 확인
            Member member = memberRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
            
            // 주문 조회
            Sales order = salesService.getOrder(orderId);
            
            // 주문 소유자 확인
            if (!order.getMember().getId().equals(member.getId())) {
                return ResponseEntity.status(403).body("권한이 없습니다.");
            }
            
            // 주문 상태 확인
            if (order.getStatus() == Sales.OrderStatus.COMPLETED) {
                return ResponseEntity.badRequest().body("입금이 완료된 주문은 삭제할 수 없습니다.");
            }
            
            // 주문 삭제
            salesService.deleteOrder(orderId);
            
            return ResponseEntity.ok("주문이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
