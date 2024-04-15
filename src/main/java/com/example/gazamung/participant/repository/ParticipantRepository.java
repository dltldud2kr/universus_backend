package com.example.gazamung.participant.repository;

import com.example.gazamung.participant.entity.Participant;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // univBattleId와 univId를 가지고 참가자 수를 세는 메소드
    int countByUnivBattleIdAndUnivId(Long univBattleId, Long univId);

    // 총 인원 수 체크
    int countByUnivBattleId(Long univBattleId);

    // 대학별 참가인원 리스트
    List<Participant> findAllByUnivIdAndUnivBattleId(Long univId, Long univBattleId);

    // 회원 ID와 대항전 ID로 참가자 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Participant p WHERE p.memberIdx = :memberIdx AND p.univBattleId = :univBattleId")
    boolean existsByMemberIdxAndUnivBattleId(@Param("memberIdx") Long memberIdx, @Param("univBattleId") Long univBattleId);
}
