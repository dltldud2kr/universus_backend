package com.example.gazamung.univBattle.service;

import com.example.gazamung.univBattle.dto.*;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UnivBattleService {

    /**
     * 대항전 생성
     * @param request
     * @return
     */
    boolean create(UnivBattleCreateRequest request);

    /**
     * 대항전 대표 참가
     * @param request
     * @return
     */
    boolean GuestLeaderAttend(GuestLeaderAttendRequest request);

    /**
     * 대항전 일반 참가
     * @param request
     * @return
     */
    boolean attend(AttendRequest request);

    /**
     * 대항전 리스트
     * @param status
     * @return
     */
    List<UnivBattle> uList(int status);


    /**
     * 해당 대학교 대항전 리스트
     * @param univId
     * @return
     */
    List<UnivBattleListRes> uList(Long univId);

    List<UnivRankListRes> rankList(Long eventId);

    /**
     * 대항전 정보
     * @param univBattleId
     * @return
     */
    Map<String, Object> info (long univBattleId);

    /**
     * 대항전 시작
     * @param univBattleId
     * @return
     */
    boolean matchStart(Long univBattleId);

    /**
     * 대항전 결과 전송 (대표자)
     * @param dto
     * @return
     */
    boolean matchResultReq(MatchResultRequest dto);

    boolean matchResultRes(MatchResultResponse dto);
}
