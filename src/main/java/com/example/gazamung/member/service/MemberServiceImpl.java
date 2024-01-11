package com.example.gazamung.member.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.EmailAuthStatus;
import com.example.gazamung.auth.JwtTokenProvider;
import com.example.gazamung.dto.TokenDto;
import com.example.gazamung.emailAuth.entity.EmailAuth;
import com.example.gazamung.emailAuth.repository.EmailAuthRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.dto.JoinRequestDto;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final EmailAuthRepository emailAuthRepository;

    /**
     * 1. 로그인 요청으로 들어온 ID, PWD 기반으로 Authentication 객체 생성
     * 2. authenticate() 메서드를 통해 요청된 Member 에 대한 검증이 진행 => loadUserByUsername 메서드를 실행.
     * 해당 메서드는 검증을 위한 유저 객체를 가져오는 부분으로써, 어떤 객체를 검증할 것인지에 대해 직접 구현
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성
     * @author 이시영
     */

    @Transactional
    public TokenDto login(String email, String password) {

        // 로그인 정보로 DB 조회  (아이디, 비밀번호)
        memberRepository.findByEmailAndPassword(email,password)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        //사용자 인증 정보를 담은 토큰을 생성함. (이메일, 비밀번호)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        //authenticationManagerBuilder를 사용하여 authenticationToken을 이용한 사용자의 인증을 시도합니다.
        // 여기서 실제로 로그인 발생  ( 성공: Authentication 반환 //   실패 : Exception 발생
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증이 된 경우 JWT 토큰을 발급  (요청에 대한 인증처리)
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        if (tokenDto.getAccessToken().isEmpty()){
            log.info(tokenDto.getAccessToken());
        }

        return tokenDto;
    }


    @Transactional
    @Override
    public TokenDto join(JoinRequestDto dto) {
        try {
            String email = dto.getEmail();
            String password = dto.getPassword();

            //해당 이메일이 존재하는지 확인.
            Optional<Member> optionalMember =  memberRepository.findByEmail(email);
            if(optionalMember.isPresent()) {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            }
            //해당 이메일이 디비에 존재하는지 확인.
            Member member = Member.builder()
                    .email(email)
                    .password(password)
                    .refreshToken(null)
                    .role(0)
                    .platform(0)
                    .regDt(LocalDateTime.now())
                    .build();
            memberRepository.save(member);

            //사용자 인증 정보를 담은 토큰을 생성함. (이메일, 비밀번호 )
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

            //authenticationManagerBuilder를 사용하여 authenticationToken을 이용한 사용자의 인증을 시도합니다.
            // 여기서 실제로 로그인 발생  ( 성공: Authentication 반환 //   실패 : Exception 발생
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // 인증이 된 경우 JWT 토큰을 발급  (요청에 대한 인증처리)
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            log.info(tokenDto.getAccessToken());
            if (tokenDto.getAccessToken().isEmpty()){
                log.info(tokenDto.getAccessToken());
                log.info("token empty");
            }
            return tokenDto;



        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TokenDto createToken(String memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        if (jwtTokenProvider.validateToken(member.getRefreshToken())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
            System.out.println("authenticationToken : "+authenticationToken);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            return tokenDto;
        } else {
            //만료된 리프레쉬 토큰.
            throw new CustomException(CustomExceptionCode.EXPIRED_JWT);
        }
    }


    /**
     * @title 이메일 인증 로직
     * @param email 회원 이메일x`
     * @return
     */

    //1.  member 테이블에 이미 있는 회원인지 확인 .   있으면 duplicated_member 예외처리
    //2.  이미 인증된 상태 (VERIFIED) 인지 확인. VERIFIED상태라면 VERIFIED_MEMBER 예외처리
    //3. UNVERIFIED 상태를 찾음. 이 때 유효시간 3분이 지났으면 EmailAuthStatus를 EXPIRED 로 변경 후 EXPIRED_AUTH 예외처리.
    // 30분이 안 지났으면 EmailAuthStatus를 VERIFIED 상태로 변경해줌.

    /*
    1. EXPIRED
    2. VERIFIED
    3. UNVERIFIED
    4. INVALID
     */

    // CustomException 예외 발생시  이 예외가 emailCheck 메서드 내에서 발생하면
    // 트랜잭션을 롤백시키기 때문에 DB에 변경 사항이 반영되지 않음.
    // @Transactional(noRollbackFor = CustomException.class)를 사용하여
    // CustomException 이 발생해도 트랜잭션을 롤백하지 않도록 설정
    @Transactional(noRollbackFor = CustomException.class)
    @Override
    public boolean emailCheck(String email, String verifCode){


        // UNVERIFIED  있을 시 만료됐는지 확인 후 인증처리
        Optional<EmailAuth> optionalEmailAuth = emailAuthRepository.findByEmailAndEmailAuthStatus(email, EmailAuthStatus.UNVERIFIED);
        if (optionalEmailAuth.isPresent()){
            EmailAuth emailAuth = optionalEmailAuth.get();
            LocalDateTime creationTime = emailAuth.getCreated();
            LocalDateTime expirationTime = creationTime.plusMinutes(3); // 3분 유효시간
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(expirationTime)) {
                emailAuth.setEmailAuthStatus(EmailAuthStatus.EXPIRED); // 만료
                updateEmailAuthStatus(emailAuth);
                throw new CustomException(CustomExceptionCode.EXPIRED_AUTH);
            } else {

                emailAuth.setEmailAuthStatus(EmailAuthStatus.VERIFIED);
                updateEmailAuthStatus(emailAuth);

            }

        }
        // UNVERIFIED 없을 시
        else {

            throw new CustomException(CustomExceptionCode.INVALID_AUTH);
        }

        return true;
    }

    @Transactional
    public void updateEmailAuthStatus(EmailAuth emailAuth) {
        emailAuthRepository.save(emailAuth);
    }

    @Transactional
    @Override
    public boolean changePw(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        member.setPassword(password);
        System.out.println("변경 비번 " + member.getPassword());

        memberRepository.save(member);


        return true;
    }

    @Override
    public boolean isMember(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        if (byEmail.isPresent()){
            return true;
        }
        return false;
    }


}
