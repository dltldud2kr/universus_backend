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

    /**
     * 이메일 인증확인 (발송된 인증번호와 동일한지 확인)
     * @param email
     * @param verifCode
     * @return
     */
    boolean emailCheck(String email, String verifCode);



    /**
     * 비밀번호 변경
     */

    boolean changePw(String email, String password);

    /**
     * 회원인지 확인
     * @param email
     * @return
     */
    boolean isMember(String email);

}
