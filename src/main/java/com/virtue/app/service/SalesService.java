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

    @Transactional
    public Sales createOrder(Long itemId, Integer count, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Sales sales = new Sales();
        sales.setTitle(item.getTitle());
        sales.setPrice(item.getPrice());
        sales.setCount(count);
        sales.setImgUrl(item.getImgUrl());
        sales.setMember(member);
        sales.setStatus(Sales.OrderStatus.PENDING);  // 초기 상태는 입금 대기
        
        return salesRepository.save(sales);
    }

    @Transactional
    public Sales updateOrderStatus(Long orderId, Sales.OrderStatus status) {
        Sales sales = getOrder(orderId);
        sales.setStatus(status);
        return salesRepository.save(sales);
    }

    public Sales getOrder(Long orderId) {
        return salesRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        salesRepository.deleteById(orderId);
    }
}
