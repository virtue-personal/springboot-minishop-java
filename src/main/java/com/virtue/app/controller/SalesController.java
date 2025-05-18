package com.virtue.app.controller;

import com.virtue.app.domain.Sales;
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

    /**
     * 주문서 화면 반환
     */
    @GetMapping("/order")
    public String orderForm(@RequestParam Long itemId, Model model) {
        model.addAttribute("item", salesService.getItem(itemId));
        return "order";
    }

    /**
     * 주문 생성 처리
     */
    @PostMapping("/order")
    public String createOrder(@RequestParam Long itemId,
                              @RequestParam Integer count,
                              Authentication auth) {
        Sales order = salesService.createOrder(itemId, count, auth.getName());
        return "redirect:/order/complete/" + order.getId();
    }

    /**
     * 주문 완료 화면 반환
     */
    @GetMapping("/order/complete/{orderId}")
    public String orderComplete(@PathVariable Long orderId, Model model) {
        model.addAttribute("order", salesService.getOrder(orderId));
        return "order-complete";
    }

    /**
     * 주문 입금 완료 처리
     */
    @PostMapping("/order/{orderId}/complete")
    @ResponseBody
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId, Authentication auth) {
        try {
            salesService.completeOrder(orderId, auth.getName());
            return ResponseEntity.ok("입금확인이 완료되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    /**
     * 주문 삭제 처리
     */
    @DeleteMapping("/sales/order/{orderId}")
    @ResponseBody
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId, Authentication auth) {
        try {
            salesService.deleteOrder(orderId, auth.getName());
            return ResponseEntity.ok("주문이 삭제되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}