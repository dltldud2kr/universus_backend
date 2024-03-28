package com.example.gazamung.member.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ")
    @SequenceGenerator(name = "MEMBER_SEQ", sequenceName = "member_sequence", allocationSize = 1)
    private Long memberIdx;

    private Long univId;    //대학
    private Long deptId;    //학과

    private String email;
    private String password;
    private String refreshToken; //리프레쉬 토큰
    private String userName;
    private String nickname;
    private String birth;
    private String gender;  // M(남자), F(여자)

    @Pattern(regexp = "^\\d{11}$", message = "11자리 핸드폰 번호를 입력하세요.")
    private String phone;
    private String address;
    private Integer role;   // 0 : USER 1 : ADMIN
//    private Integer univAuth;   //대학인증여부
//    private Integer isActive;   //카카오 추가 기입 정보 0, 1
    private LocalDateTime regDt;
    private LocalDateTime udtDt;

    private String oneLineIntro;    // 한 줄 소개

    private Long representIdx;  // 대표 사진 idx


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    //authentication.getName()이 이것을 반환함.
    @Override
    public String getUsername() {
        return email;
    }

    public Long getMemberIdx() {
        return memberIdx;
    }


    // idx 카카오 고유번호  (pk값) 을 password 로 둠.
    // password로 둔 이유는 스프링 시큐리티 UserDetails객체를 사용하기 때문
    // 이 객체는 userName과 password를 받아야함.
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}