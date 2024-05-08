package com.example.gazamung.deptBattle.repository;

import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeptBattleRepository extends JpaRepository<DeptBattle, Long> {

    List<DeptBattle> findByMatchStatus(MatchStatus matchStatus);
}
