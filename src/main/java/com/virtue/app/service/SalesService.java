package com.virtue.app.service;

import com.virtue.app.domain.Item;
import com.virtue.app.domain.Member;
import com.virtue.app.domain.Sales;
import com.virtue.app.repository.ItemRepository;
import com.virtue.app.repository.MemberRepository;
import com.virtue.app.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 상품 ID로 상품 조회 (주문서용)
     */
    @Transactional(readOnly = true)
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    /**
     * 주문 생성
     */
    @Transactional
    public Sales createOrder(Long itemId, Integer count, String username) {
        Member member = getMemberByUsername(username);
        Item item = getItem(itemId);

        Sales sales = new Sales();
        sales.setTitle(item.getTitle());
        sales.setPrice(item.getPrice());
        sales.setCount(count);
        sales.setImgUrl(item.getImgUrl());
        sales.setMember(member);
        sales.setStatus(Sales.OrderStatus.PENDING);

        return salesRepository.save(sales);
    }

    /**
     * 주문 ID로 주문 조회
     */
    @Transactional(readOnly = true)
    public Sales getOrder(Long orderId) {
        return salesRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    /**
     * 주문 상태 변경 (입금 완료 처리)
     */
    @Transactional
    public void completeOrder(Long orderId, String username) {
        Sales order = getOrder(orderId);
        validateOrderOwnership(order, username);

        if (order.getStatus() == Sales.OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 입금이 완료된 주문입니다.");
        }

        order.setStatus(Sales.OrderStatus.COMPLETED);
        salesRepository.save(order);
    }

    /**
     * 주문 삭제 처리
     */
    @Transactional
    public void deleteOrder(Long orderId, String username) {
        Sales order = getOrder(orderId);
        validateOrderOwnership(order, username);

        if (order.getStatus() == Sales.OrderStatus.COMPLETED) {
            throw new IllegalStateException("입금이 완료된 주문은 삭제할 수 없습니다.");
        }

        salesRepository.deleteById(orderId);
    }

    /**
     * 사용자명으로 회원 조회
     */
    @Transactional(readOnly = true)
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    /**
     * 주문 소유자 확인
     */
    private void validateOrderOwnership(Sales order, String username) {
        Long memberId = getMemberByUsername(username).getId();
        if (!order.getMember().getId().equals(memberId)) {
            throw new SecurityException("권한이 없습니다.");
        }
    }
}