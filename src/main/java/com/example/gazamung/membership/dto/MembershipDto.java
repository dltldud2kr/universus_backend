package com.example.gazamung.membership.dto;

import com.example.gazamung.moim.service.MoimServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MembershipDto {

    private Long membershipId;

    private Long memberIdx;
    private Long moimId;

    private MoimServiceImpl.MembershipStatus status;

    private LocalDateTime joinRequestDt;    // 가입 요청 시간
    private LocalDateTime joinRequestRejectedDt;  // 가입 요청 거절 시간

    private LocalDateTime regDt;    // 가입한 시간

}
