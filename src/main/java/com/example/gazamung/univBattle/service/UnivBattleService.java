package com.example.gazamung.univBattle.service;

import com.example.gazamung.univBattle.UnivBattleAttendRequest;
import com.example.gazamung.univBattle.UnivBattleCreateRequest;
import com.example.gazamung.university.entity.University;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnivBattleService {


    boolean create(UnivBattleCreateRequest request);

    boolean attend(UnivBattleAttendRequest request);
}
