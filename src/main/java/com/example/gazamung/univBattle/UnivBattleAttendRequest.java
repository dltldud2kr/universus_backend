package com.example.gazamung.univBattle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnivBattleAttendRequest {

    private long univBattleId;   // 해당 대항전 ID 값
    private long guestLeader ;   // 대표자 memberIdx

}
