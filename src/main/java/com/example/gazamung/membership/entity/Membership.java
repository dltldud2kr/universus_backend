package com.example.gazamung.membership.entity;

import com.example.gazamung.moim.service.MoimServiceImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Membership {

    @Id
    @GeneratedValue
    private Long membershipId;

    private Long memberIdx;
    private Long moimId;

    private MoimServiceImpl.MembershipStatus status;

    private LocalDateTime joinRequestDt;    // 가입 요청 시간
    private LocalDateTime joinRequestRejectedDt;  // 가입 요청 거절 시간

    private LocalDateTime regDt;    // 가입한 시간

}
