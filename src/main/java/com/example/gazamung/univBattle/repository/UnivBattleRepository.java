package com.example.gazamung.univBattle.repository;

import com.example.gazamung._enum.Status;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnivBattleRepository extends JpaRepository<UnivBattle, Long> {

    List<UnivBattle> findByStatus(Status status);
}
