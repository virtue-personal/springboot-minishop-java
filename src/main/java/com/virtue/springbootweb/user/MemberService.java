package com.virtue.springbootweb.user;

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

        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isPresent()){
            throw new Exception("존재하는아이디");
        }


        if (username.length() < 8 || password.length() < 8){
            throw new Exception("너무짧음");
        }
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setDisplayName(displayName);
        memberRepository.save(member);
    }
}
