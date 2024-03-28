package com.example.gazamung.member.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.dto.TokenDto;
import com.example.gazamung.emailAuth.dto.EmailCheckDto;
import com.example.gazamung.emailAuth.dto.EmailDto;
import com.example.gazamung.emailAuth.service.EmailService;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.dto.*;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "회원 API", description = "")
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final EmailService emailService;

    @Operation(summary = "Access Token 발급 요청", description = "" +
            "RefreshToken으로 Access Token 발급을 요청합니다." +
            "\n### 토큰 별 유효기간" +
            "\n- AccessToken: 2시간" +
            "\n- RefreshToken: 3일" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 401: 만료된 토큰이거나, 잘못된 토큰" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
    })
    @PostMapping("/auth/token")
    public ResultDTO<TokenDto> getAccessToken(@RequestBody Long memberIdx) {
        try {
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "토큰이 갱신 되었습니다.", memberService.createToken(memberIdx));
        } catch (CustomException e) {
            memberService.createToken(memberIdx);
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "이메일 인증번호 전송 ", description = "" +
            "이메일 인증번호 전송" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })

    @PostMapping("/emailSend")
    public ResultDTO<Object> emailSend(@RequestBody EmailDto dto) throws Exception {
        try {
            boolean result = memberService.isMember(dto.getEmail());

            //회원이 있을 시 예외처리
            if (result){
                throw new CustomException(CustomExceptionCode.DUPLICATED_MEMBER);
            }
            String email = emailService.sendEmailVerification(dto.getEmail());
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "인증번호가 정상적으로 발송되었습니다.", email);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), "메일 발송 중 문제가 발생했습니다.", null);
        }
    }

    @Operation(summary = "회원 가입 시 이메일 인증 처리", description = "" +
            "회원 가입 시 이메일 인증 처리." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })

    // 인증한 이메일의 이메일인증여부를 변경
    @PostMapping("/email/auth")
    public ResultDTO<Object> emailCheck(@RequestBody EmailCheckDto dto) {
        try {
            boolean result = memberService.isMember(dto.getEmail());
            if (result == true){
                throw new CustomException(CustomExceptionCode.DUPLICATED_MEMBER);
            }
            memberService.emailCheck(dto.getEmail(),dto.getVerifcode());
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "이메일 인증이 정상적으로 되었습니다.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "비밀번호 찾기 시 이메일 인증번호 전송", description = "" +
            "비밀번호 찾기 시 이메일 인증번호 전송." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/pwFind/emailSend")
    public ResultDTO<Object> pwFindEmailSend(@RequestBody PasswordFoundDto dto) throws Exception{

        try {
            boolean result = memberService.isMember(dto.getEmail());

            // 회원이 없을 시 예외처리
            if (!result){
                throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
            }
            emailService.sendEmailVerification(dto.getEmail());
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "인증번호가 메일로 발송되었습니다.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), "메일 발송 중 문제가 발생했습니다.", null);
        }
    }



    @Operation(summary = "비밀번호 찾기 시 이메일 인증 처리", description = "" +
            "비밀번호 찾기 시 이메일 인증 처리." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/pwFind/email/auth")
    public ResultDTO<Object> findPwEmailCheck(@RequestBody EmailCheckDto dto){

        try {
            boolean result = memberService.isMember(dto.getEmail());
            if (result == false){
                throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
            }
            memberService.emailCheck(dto.getEmail(),dto.getVerifcode());
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "이메일 인증이 정상적으로 되었습니다.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "비밀번호 변경 ", description = "" +
            " 비밀번호 변경." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/pwChange")
    public ResultDTO<Object> pwChange(@RequestBody PwChangeDto dto){

        try {
            memberService.changePw(dto.getEmail(),dto.getPassword());
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "비밀번호 변경 완료", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "로그인 요청", description = "" +
            "회원 로그인을 요청하고 토큰을 발급합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 403: 회원정보 인증 실패" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- SUCCESS: 로그인 성공 및 정상 토큰 발급" +
            "\n- NOT_FOUND_EMAIL: 요청한 이메일 가입자가 존재하지 않음")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
    })
    @PostMapping("/auth/login")
    public ResultDTO<Map<String, Object>> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            System.out.println(email + password);

            Map<String, Object> result = memberService.login(email, password);

            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "로그인 성공", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "회원가입 요청", description = "" +
            "임시 회원가입을 요청합니다." +
            "테스트 용도로 확인만 해주세요." +
            "테스트가 끝나면 token을 반환하지 않게 바꿀 예정." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 201: 회원가입 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- DUPLICATED: 동일한 이메일이 존재합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
    })
    @PostMapping("/auth/join")
    public ResultDTO join(@RequestBody JoinRequestDto joinRequestDto) {
        try {
            Map<String, Object> result = memberService.join(joinRequestDto);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "회원가입이 완료되었습니다.", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



    @PostMapping("/auth/kakao/app")
    public ResultDTO kakaoFlutterLogin(@RequestBody KakaoFlutterRequest request) {

        String email = request.getEmail();
        String kakaoIdx = request.getKakaoIdx();
        // 이메일값으로 멤버가 존재하는지 확인.
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            try{
                Map<String,Object> result = memberService.login(email,kakaoIdx);
                return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "로그인 성공", result);
            } catch (CustomException e){
                return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
            }
        } else {
            try{
                Map<String, Object> result = memberService.kakaoJoin(email,kakaoIdx);
                return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "회원가입이 완료되었습니다.", result);
            } catch (CustomException e) {
                return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
            }
        }
    }


    @GetMapping("/member/all")
    public List<MemberDto> allMember(){
        List<MemberDto> members = memberService.getAllMembers();
        return members;
    }

    @GetMapping("/member/profile")  /** 프로필 조회 **/
    public ResponseEntity<ProfileDto> getMemberInfo(@RequestParam Long memberIdx){

        ProfileDto profileDto = memberService.getMemberInfo(memberIdx);

        // 멤버 정보 반환
        return ResponseEntity.ok(profileDto);

    }
    @PostMapping("/member/updateProfile")     /** 프로필 수정 **/
    public ResultDTO updateProfile(@RequestBody ProfileDto dto){
        try{
            return ResultDTO.of(memberService.updateProfile(dto), ApiResponseCode.SUCCESS.getCode(), "프로필 수정 완료", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PostMapping("/member/updatePw")    /** 비밀번호 변경 **/
    public ResultDTO updatePw(@RequestBody PwChangeDto dto){

        try{
            Long result = memberService.updatePw(dto.getMemberIdx(), dto.getPassword());
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "비밀번호 변경 완료", result);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @DeleteMapping("/member/withDraw")    /** 회원 탈퇴 **/
    public ResultDTO withDraw(@RequestParam Long memberIdx, String password){
        try{
            return ResultDTO.of(memberService.withDraw(memberIdx, password), ApiResponseCode.SUCCESS.getCode(), "회원 탈퇴 완료", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PostMapping("/member/uploadImage")
    public ResultDTO uploadImage(@RequestBody ProfileDto dto) {
        try {
            Map<String, Object> result = memberService.uploadImage(dto);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "프로필 사진 업로드가 완료되었습니다.", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



}
