package com.example.gazamung.emailAuth.service;


import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.EmailAuthStatus;
import com.example.gazamung.emailAuth.entity.EmailAuth;
import com.example.gazamung.emailAuth.repository.EmailAuthRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private EmailAuthRepository emailAuthRepository;

    public static String ePw = createKey();

    //todo emailHtml 을 파라미터로 빼서 동적으로 사용할 수 있도록 해주세요.
    private MimeMessage createMessage(String to) throws Exception {

        ePw = createKey();
        // 이메일 내용에 버튼을 추가한 HTML

        // HTML 이메일 템플릿
        String msgg = "<html><head></head><body style='font-family: Arial, sans-serif;'>";
        msgg += "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 2px solid #007bff; border-radius: 5px;'>";
        msgg += "<h1 style='color: #007bff; text-align: center;'>가자멍 계정 인증</h1>";
        msgg += "<p style='text-align: center;'>안녕하세요, Universe 입니다.</p>";
        msgg += "<p style='text-align: center;'>아래 코드를 복사하여 인증해주세요.</p>";
        msgg += "<div style='text-align: center; margin-top: 20px;'>";
        msgg += "<div style='display: inline-block; background-color: #007bff; color: #fff; padding: 10px 20px; border-radius: 5px; font-size: 18px;'>" + ePw + "</div>";
        msgg += "</div>";
        msgg += "<p style='text-align: center; margin-top: 20px;'>감사합니다.</p>";
        msgg += "</div></body></html>";

        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);
        message.setSubject("Universe 계정 인증");
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("dltldud2kr@gmail.com", "leesiyoung"));

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    @Override
    public String sendEmailVerification(String email) throws Exception {

//        if (memberService.getMember(email) != null) {
//            throw new CustomException(CustomExceptionCode.DUPLICATED_MEMBER);
//        }


        //todo 사용하는 이메일 라이브러리에서 세팅하는게 모르겠지만 무제한으로 계속 인증메일 전송하는 보안처리를 해야할것 같음.
        //todo n분동안 n개이상의 메일을 전송한 사람이라면 특정 시간동안 잠시 메일발송을 중단시키거나 하는 처리



        //전에 쌓인 인증되지 않은 EmailAuth 값들을 전부 "INVALID"로 변경
        //todo 이부분은 JPA로 find 후 save 하는것보다 myBatis 에서 update 1번 날려주는게 더 효율적일거같음.
        //todo MemberMapper에다가 작성하면 될듯함.
        invalidateUnverifiedEmailAuths(email);


        // 이메일 보내는 로직
        emailSender(email);

        return email;
    }

    /**
     * 전에 쌓인 인증되지 않은 EmailAuth 값들을 전부 "INVALID"로 변경
     * @param email
     */
    public void invalidateUnverifiedEmailAuths(String email){
        List<EmailAuth> emailAuthList = emailAuthRepository.findListByEmailAndEmailAuthStatus(email, EmailAuthStatus.UNVERIFIED);

        if (!emailAuthList.isEmpty()){
            for (EmailAuth emailAuth : emailAuthList) {
                emailAuth.setEmailAuthStatus(EmailAuthStatus.INVALID);
            }
            emailAuthRepository.saveAll(emailAuthList);
        }

    }

    /**
     * 인증 이메일 발송
     * @param email
     * @throws Exception
     */



    public void emailSender(String email) throws Exception {

        // 이메일 보내는 로직
        MimeMessage message = createMessage(email);
        try {
            emailSender.send(message);
            //메일 인증 테이블
            EmailAuth newAuth = EmailAuth.builder()
                    .email(email)
                    .emailAuthStatus(EmailAuthStatus.UNVERIFIED)
                    .regDt(LocalDateTime.now())
                    .verifCode(ePw)
                    .build();
            emailAuthRepository.save(newAuth);


        } catch (MailException e) {
            e.printStackTrace();
            throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        }
    }
}