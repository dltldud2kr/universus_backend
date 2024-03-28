package com.example.gazamung.member.service;

import com.example.gazamung.S3FileUploader.UploadImage;
import com.example.gazamung.S3FileUploader.UploadService;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.EmailAuthStatus;
import com.example.gazamung.auth.JwtTokenProvider;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.dto.TokenDto;
import com.example.gazamung.emailAuth.entity.EmailAuth;
import com.example.gazamung.emailAuth.repository.EmailAuthRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.mapper.MemberMapper;
import com.example.gazamung.member.dto.JoinRequestDto;
import com.example.gazamung.member.dto.MemberDto;
import com.example.gazamung.member.dto.ProfileDto;
import com.example.gazamung.member.dto.UpdateProfileDto;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final EmailAuthRepository emailAuthRepository;

    private final MemberMapper memberMapper;

    private final UploadService uploadService;

    /**
     * 1. 로그인 요청으로 들어온 ID, PWD 기반으로 Authentication 객체 생성
     * 2. authenticate() 메서드를 통해 요청된 Member 에 대한 검증이 진행 => loadUserByUsername 메서드를 실행.
     * 해당 메서드는 검증을 위한 유저 객체를 가져오는 부분으로써, 어떤 객체를 검증할 것인지에 대해 직접 구현
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성
     *
     * @author 이시영
     */

    @Transactional
    public Map<String, Object> login(String email, String password) {

        // 로그인 정보로 DB 조회  (아이디, 비밀번호)
        Member member = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        //사용자 인증 정보를 담은 토큰을 생성함. (이메일, 비밀번호)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        //authenticationManagerBuilder를 사용하여 authenticationToken을 이용한 사용자의 인증을 시도합니다.
        // 여기서 실제로 로그인 발생  ( 성공: Authentication 반환 //   실패 : Exception 발생
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증이 된 경우 JWT 토큰을 발급  (요청에 대한 인증처리)
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        if (tokenDto.getAccessToken().isEmpty()) {
            log.info(tokenDto.getAccessToken());
        }


        Map<String, Object> response = new HashMap<>();
        response.put("tokenDto", tokenDto);
        response.put("memberIdx", member.getMemberIdx());

        return response;
    }


    // 최신 카카오 로그인

    @Transactional
    @Override
    public Map<String, Object> kakaoJoin(String email, String password) {
        try {

            //해당 이메일로 가입된 회원이 있는지 확인
            Optional<Member> optionalMember = memberRepository.findByEmail(email);
            if (optionalMember.isPresent()) {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            }

            Member member = Member.builder()
                    .email(email)   // 이메일
                    .password(password) //비밀번호
                    .refreshToken(null)
                    .role(0)
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
            if (tokenDto.getAccessToken().isEmpty()) {
                log.info(tokenDto.getAccessToken());
                log.info("token empty");
            }

//            int infoCheck = member.getIsActive();

            Map<String, Object> response = new HashMap<>();
            response.put("tokenDto", tokenDto);
            response.put("memberIdx", member.getMemberIdx());
//            response.put("infoCheck", infoCheck);

            return response;


        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Transactional
    @Override
    public Map<String, Object> join(JoinRequestDto dto) {
        try {
            String email = dto.getEmail();
            String password = dto.getPassword();

            //해당 이메일로 가입된 회원이 있는지 확인
            Optional<Member> optionalMember = memberRepository.findByEmail(email);
            if (optionalMember.isPresent()) {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            }
//            Optional<EmailAuth> optionalEmailAuth = emailAuthRepository.findByEmailAndEmailAuthStatus(email,EmailAuthStatus.VERIFIED);
//            emailAuthRepository.findByEmailAndEmailAuthStatus(email,EmailAuthStatus.VERIFIED)
//                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_COMPLETE_AUTH));


            Member member = Member.builder()
                    .email(email)   // 이메일
                    .password(password) //비밀번호
                    .refreshToken(null)
                    .role(0)
                    .birth(dto.getBirth())  // 생년월일
                    .gender(dto.getGender())    // 성별 (M, F)
                    .nickname(dto.getNickname())    // 닉네임
                    .userName(dto.getUserName())    // 사용자 이름
                    .univId(dto.getUnivId())        // 대학교 ID값
                    .phone(dto.getPhone())  // 휴대폰번호
                    .address(dto.getAddress())  //주소
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
            if (tokenDto.getAccessToken().isEmpty()) {
                log.info(tokenDto.getAccessToken());
                log.info("token empty");
            }


            Map<String, Object> response = new HashMap<>();
            response.put("tokenDto", tokenDto);
            response.put("memberIdx", member.getMemberIdx());

            return response;


        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public TokenDto createToken(Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        if (jwtTokenProvider.validateToken(member.getRefreshToken())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
            System.out.println("authenticationToken : " + authenticationToken);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            return tokenDto;
        } else {
            //만료된 리프레쉬 토큰.
            throw new CustomException(CustomExceptionCode.EXPIRED_JWT);
        }
    }

    @Override
    public String getReturnAccessToken(String code, HttpServletRequest request) {
        String access_token = "";
        String refresh_token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //HttpURLConnection 설정 값 셋팅
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // buffer 스트림 객체 값 셋팅 후 요청
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
//           sb.append("&client_id=b22a0873d0ccefbc5f331106fa7b9287");  // REST API 키

            String origin = request.getHeader("Origin"); //요청이 들어온 Origin을 가져옵니다.
            sb.append("&client_id=ccf25614050bf5afb0bf4c82541cebb8");  // REST API 키

            sb.append("&redirect_uri=http://localhost:8080/auth/kakao/callback");
            // 테스트 서버, 퍼블리싱 서버 구분
            /*
            if("http://localhost:8080".equals(origin)){

                sb.append("&redirect_uri=http://localhost:8080/auth/kakao/callback"); // 앱 CALLBACK 경로
            } else {
                sb.append("&redirect_uri=https://app.lunaweb.dev/auth/kakao/callback"); // 다른 경로
            }

             */
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //  RETURN 값 result 변수에 저장
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String br_line = "";
            String result = "";

            while ((br_line = br.readLine()) != null) {
                result += br_line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);


            // 토큰 값 저장 및 리턴
            access_token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token: " + access_token);
            System.out.println("refresh_token: " + refresh_token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_token;
    }

    @Override
    public Map<String, Object> getUserInfo(String access_token) {
        Map<String, Object> resultMap = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String br_line = "";
            String result = "";

            while ((br_line = br.readLine()) != null) {
                result += br_line;
            }
            log.info("response:" + result);


            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            log.warn("element:: " + element);
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            log.warn("id:: " + element.getAsJsonObject().get("id").getAsString());
            String id = element.getAsJsonObject().get("id").getAsString();
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();
            // 프로필 이미지 정보 반환
            String profileImage = properties.getAsJsonObject().get("profile_image").getAsString();

            log.warn("email:: " + email);
            resultMap.put("nickname", nickname);
            resultMap.put("id", id);
            resultMap.put("email", email);
            // Map에 프로필 이미지 정보를 추가합니다.
            resultMap.put("profile_image", profileImage);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public List<MemberDto> getAllMembers() {
        MemberDto parameter = new MemberDto();
        List<MemberDto> list = memberMapper.selectList(parameter);
        return list;
    }

    @Override
    public Long updatePw(Long memberIdx, String password) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        member.setPassword(password);
        memberRepository.save(member);
        return memberIdx;
    }

    @Override
    public boolean withDraw(Long memberIdx, String password) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        if (!member.getPassword().equals(password)) {
            throw new CustomException(CustomExceptionCode.DIFFERENT_PASSWORD);
        }
        memberRepository.delete(member);
        return true;
    }



    /**
     * @param dto
     * @title 프로필 수정
     * @created 24.03.27 이승열
     * @description 닉네임, 전화번호, 한 줄 소개 동시 처리.
     *              값이 null 이면 기존 값 유지
     *              학과 API 는 따로 만들어 추가
     */
    @Override
    public boolean updateProfile(ProfileDto dto) {
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 닉네임 중복 검사
        if (dto.getNickname() != null && !dto.getNickname().equals(member.getNickname())) {
            memberRepository.findByNickname(dto.getNickname()).ifPresent(existingMember -> {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            });
            member.setNickname(dto.getNickname());
        }

        // 핸드폰 번호 중복 검사
        if (dto.getPhone() != null && !dto.getPhone().equals(member.getPhone())) {
            memberRepository.findByPhone(dto.getPhone()).ifPresent(existingMember -> {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            });
            member.setPhone(dto.getPhone());
        }

        // 글자 수 제한
        if (dto.getOneLineIntro() != null && dto.getOneLineIntro().length() > 100) {
            throw new CustomException(CustomExceptionCode.CHARACTER_LIMIT);
        }

        if (dto.getOneLineIntro() != null) {
            member.setOneLineIntro(dto.getOneLineIntro());
        }

        memberRepository.save(member);
        return true;
    }


    @Override
    public Map<String, Object> uploadImage(ProfileDto dto) {

        memberRepository.findById(dto.getMemberIdx()).orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        List<Map<String, Object>> uploadImage = uploadService.upload(dto.getProfileImage(), dto.getMemberIdx(), AttachmentType.PROFILE, dto.getMemberIdx());

        Map<String, Object> result = new HashMap<>();
        result.put("uploadedImages", uploadImage);

        return result;
    }

    @Override
    public void updateImage(UpdateProfileDto dto) {
        try {
            //해당 모임에 업로드 등록되어있는 이미지를 검색합니다.
            List<UploadImage> imageByAttachmentType = uploadService.getImageByAttachmentType(AttachmentType.PROFILE, dto.getMemberIdx());
            String[] removeTarget = new String[imageByAttachmentType.size() + 1];

            int removeCount = 0;
            //업로드된 이미지가 잇는 경우
            try {
                if (imageByAttachmentType.size() > 0) {
                    for (UploadImage file : imageByAttachmentType) {
                        // 문자열에서 ".com/" 다음의 정보를 추출
                        int startIndex = file.getImageUrl().indexOf(".com/") + 5;
                        String result = file.getImageUrl().substring(startIndex);
                        removeTarget[removeCount] = result;
                        removeCount++;
                    }
                    //등록되어있는 파일 정보 삭제 요청.
                    uploadService.removeS3Files(removeTarget);
                    //데이터베이스에 맵핑되어있는 정보삭제
                    uploadService.removeDatabaseByReviewIdx(dto.getMemberIdx());
                }
            } catch (CustomException e) {
                throw new CustomException(CustomExceptionCode.SERVER_ERROR);
            }

            //새롭게 요청온 업로드 이미지를  버킷에 업로드함.
            uploadService.upload(dto.getProfileImage(), dto.getMemberIdx(), AttachmentType.PROFILE, dto.getMemberIdx());

            //업로드된 이미지 정보를 데이터베이스
            List<UploadImage> getRepresentIdx = uploadService.getImageByAttachmentType(AttachmentType.PROFILE, dto.getMemberIdx());
        }
        catch (CustomException e) {
            System.err.println("modifyJournal Exception : " + e);
        }
    }


    /**
     * @param memberIdx
     * @title 회원 프로필 정보 반환
     * @created 24.03.27 이승열
    **/
    public ProfileDto getMemberInfo(Long memberIdx) {
        return memberRepository.findByMemberIdx(memberIdx)
                .map(member -> {
                    ProfileDto profileDto = new ProfileDto();
                    profileDto.setMemberIdx(memberIdx);
                    profileDto.setUserName(member.getUsername());
                    profileDto.setNickname(member.getNickname());
                    profileDto.setUnivId(member.getUnivId());
                    profileDto.setDeptId(member.getDeptId());
                    profileDto.setPhone(member.getPhone());
                    profileDto.setOneLineIntro(member.getOneLineIntro());
                    return profileDto;
                })
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));
    }

    /**
     * @param email 회원 이메일x`
     * @return
     * @title 이메일 인증 로직
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
    public boolean emailCheck(String email, String verifCode) {


        // UNVERIFIED  있을 시 만료됐는지 확인 후 인증처리
        Optional<EmailAuth> optionalEmailAuth = emailAuthRepository.findByEmailAndEmailAuthStatus(email, EmailAuthStatus.UNVERIFIED);
        if (optionalEmailAuth.isPresent()) {
            EmailAuth emailAuth = optionalEmailAuth.get();
            LocalDateTime creationTime = emailAuth.getRegDt();
            LocalDateTime expirationTime = creationTime.plusMinutes(3); // 3분 유효시간
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(expirationTime)) {
                emailAuth.setEmailAuthStatus(EmailAuthStatus.EXPIRED); // 만료
                updateEmailAuthStatus(emailAuth);
                throw new CustomException(CustomExceptionCode.EXPIRED_AUTH);
            } else {

                if (!verifCode.equals(emailAuth.getVerifCode())) {
                    log.info("verifCode");
                    throw new CustomException(CustomExceptionCode.INVALID_VERIF_CODE);
                }

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
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        member.setPassword(password);
        System.out.println("변경 비번 " + member.getPassword());

        memberRepository.save(member);


        return true;
    }

    @Override
    public boolean isMember(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            return true;
        }
        return false;
    }


}
