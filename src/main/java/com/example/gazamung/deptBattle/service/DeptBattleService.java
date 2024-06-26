package com.example.gazamung.deptBattle.service;

import com.example.gazamung.deptBattle.dto.*;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.MatchResultRequest;
import com.example.gazamung.univBattle.dto.MatchResultResponse;
import com.example.gazamung.univBattle.dto.UnivBattleListRes;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DeptBattleService {

    /**
     * 대항전 생성
     * @param request
     * @return
     */
    boolean create(DeptBattleCreateRequest request);

    /**
     * 대항전 일반 참가
     * @param request
     * @return
     */
    boolean attend(DeptBattleAttendRequest request);

    /**
     * 대항전 대표 참가
     * @param request
     * @return
     */
    boolean GuestLeaderAttend(DeptGuestLeaderAttendRequest request);

    /**
     * 대항전 리스트
     * @param status
     * @return
     */
    List<DeptBattle> list (int status, Long memberIdx);

    /**
     * 대항전 정보
     * @param deptBattleId
     * @return
     */
    Map<String, Object> info (long deptBattleId);


    boolean matchStart(long deptBattleId);


    /**
     * 대항전 결과 전송 (대표자)
     * @param dto
     * @return
     */
    boolean matchResultReq(DeptMatchResultReq dto);

    boolean matchResultRes(DeptMatchResultRes dto);

    List<DeptBattleListRes> dList(Long memberIdx, Long deptId);
}
