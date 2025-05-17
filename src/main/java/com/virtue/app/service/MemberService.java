package com.virtue.app.service;

import com.virtue.app.domain.Member;
import com.virtue.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveUser(String username, String password, String displayName) throws Exception {
        // 아이디 중복 체크
        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isPresent()) {
            throw new Exception("이미 사용중인 아이디입니다.");
        }

        // 입력값 유효성 검사
        if (displayName == null || displayName.trim().length() == 0 || displayName.length() > 5) {
            throw new Exception("이름은 1-5자 사이로 입력해주세요.");
        }
        if (username == null || username.length() < 5 || username.length() > 20) {
            throw new Exception("아이디는 5-20자 사이로 입력해주세요.");
        }
        if (password == null || password.length() < 8 || password.length() > 16) {
            throw new Exception("비밀번호는 8-16자 사이로 입력해주세요.");
        }

        // 회원 정보 저장
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setDisplayName(displayName);
        memberRepository.save(member);
    }
}
