package com.example.gazamung.like.entity;

import com.example.gazamung.member.entity.Member;
import com.example.gazamung.moim.entity.Moim;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;
import java.security.PublicKey;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
@Table(name = "Likes")
public class Like {

    @Id
    @GeneratedValue
    private Long likeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberIdx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moimId")
    private Moim moim;


}
