package com.example.gazamung.univBattle.service;

import com.example.gazamung.univBattle.dto.UnivBattleAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import org.springframework.stereotype.Service;

@Service
public interface UnivBattleService {


    boolean create(UnivBattleCreateRequest request);

    boolean attend(UnivBattleAttendRequest request);
}
