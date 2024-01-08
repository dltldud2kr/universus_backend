package com.example.gazamung.member.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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
@Slf4j  // 로그를 남기기 위한 어노테이션
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    private Long idx;

    private String email;
    private String password;
    private String refreshToken; //리프레쉬 토큰
    private String userName;
    private String nickname;
    private int role;   // 0 : USER 1 : ADMIN
    private int platform;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;


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

    public Long getIdx() {
        return idx;
    }


    // idx 카카오 고유번호  (pk값) 을 password 로 둠.
    // password로 둔 이유는 스프링 시큐리티 UserDetails객체를 사용하기 때문
    // 이 객체는 userName과 password를 받아야함.
    // 우리 프로젝트는 email과 카카오 고유 idx 값만 확인해서 로그인 시키기 때문에 password 대신 idx 를 넣음
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