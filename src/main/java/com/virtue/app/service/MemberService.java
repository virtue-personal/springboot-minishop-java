package com.virtue.app.service;

import com.virtue.app.domain.Member;
import com.virtue.app.domain.Sales;
import com.virtue.app.dto.MemberDto;
import com.virtue.app.dto.OrderDto;
import com.virtue.app.repository.MemberRepository;
import com.virtue.app.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SalesRepository salesRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 저장 (중복 및 유효성 검증 포함)
     */
    @Transactional
    public void saveUser(String username, String password, String displayName) throws Exception {
        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isPresent()) {
            throw new Exception("이미 사용중인 아이디입니다.");
        }

        if (displayName == null || displayName.trim().length() == 0 || displayName.length() > 5) {
            throw new Exception("이름은 1-5자 사이로 입력해주세요.");
        }
        if (username == null || username.length() < 5 || username.length() > 20) {
            throw new Exception("아이디는 5-20자 사이로 입력해주세요.");
        }
        if (password == null || password.length() < 8 || password.length() > 16) {
            throw new Exception("비밀번호는 8-16자 사이로 입력해주세요.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setDisplayName(displayName);
        memberRepository.save(member);
    }

    /**
     * 로그인된 사용자의 주문 목록을 페이지로 조회
     */
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrderListForMyPage(String username, int page) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        PageRequest pageRequest = PageRequest.of(page - 1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Sales> ordersPage = salesRepository.findByMember(member, pageRequest);

        return ordersPage.map(order -> new OrderDto(
                order.getId(),
                order.getTitle(),
                order.getPrice(),
                order.getCount(),
                order.getImgUrl(),
                order.getStatus(),
                order.getCreatedAt()
        ));
    }

    /**
     * 사용자의 주문 삭제 처리 (권한 확인 포함)
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteOrder(String username, Long orderId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Sales order = salesRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMember().getId().equals(member.getId())) {
            return false;
        }

        salesRepository.deleteById(orderId);
        return true;
    }

    /**
     * ID로 사용자 정보 조회 (DTO로 변환)
     */
    @Transactional(readOnly = true)
    public MemberDto getUserById(Long id) {
        Member result = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new MemberDto(result.getUsername(), result.getDisplayName());
    }
}