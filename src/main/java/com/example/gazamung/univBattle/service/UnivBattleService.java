package com.example.gazamung.univBattle.service;

import com.example.gazamung.univBattle.dto.AttendRequest;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnivBattleService {


    boolean create(UnivBattleCreateRequest request);

    boolean GuestLeaderAttend(GuestLeaderAttendRequest request);

    boolean attend(AttendRequest request);

    List<UnivBattle> list (int status);
}
