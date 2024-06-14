package com.example.gazamung.univBattle.repository;

import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnivBattleRepository extends JpaRepository<UnivBattle, Long> {

    List<UnivBattle> findByMatchStatus(MatchStatus matchStatus);

    // 승리,패배 대학에 속한 univId 로 리스트 반환 (경기과 종료된 리스트만 반환하기위해)
    @Query("SELECT u FROM UnivBattle u WHERE u.winUniv = :univId OR u.loseUniv = :univId")
    List<UnivBattle> findByUnivId(Long univId);

    List<UnivBattle> findAllByOrderByRegDtDesc();

    List<UnivBattle> findByMatchStatusOrderByRegDtDesc(MatchStatus matchStatus);
}
