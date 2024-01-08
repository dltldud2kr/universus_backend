package com.example.gazamung.member.service;

import com.example.gazamung.dto.TokenDto;
import com.example.gazamung.member.dto.JoinRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    /**
     * 회원 등록
     */
    TokenDto join(JoinRequestDto dto);

    /**
     * 회원 로그인
     * @param email
     * @param password
     * @return
     */
    TokenDto login(String email, String password);


    /**
     * 토큰 생성
     * @param memberIdx
     * @return
     */
    TokenDto createToken(String memberIdx);
}
