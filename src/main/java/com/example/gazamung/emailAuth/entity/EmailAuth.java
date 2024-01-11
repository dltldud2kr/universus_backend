package com.example.gazamung.emailAuth.entity;

import com.example.gazamung._enum.EmailAuthStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "email_auth")
@Data
public class EmailAuth {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_auth_seq")
    @SequenceGenerator(name = "email_auth_seq", sequenceName = "email_auth_seq", allocationSize = 1)
    private Long id;


    private LocalDateTime created;
    private String email;
    private String verifCode;

    @Enumerated(EnumType.STRING)
    private EmailAuthStatus emailAuthStatus;

}
