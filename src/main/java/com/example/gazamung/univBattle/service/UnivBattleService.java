package com.example.gazamung.univBattle.service;

import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.univBattle.dto.AttendRequest;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UnivBattleService {


    boolean create(UnivBattleCreateRequest request);

    boolean GuestLeaderAttend(GuestLeaderAttendRequest request);

    boolean attend(AttendRequest request);

    List<UnivBattle> list (int status);

    Map<String, Object> info (long univBattleId);
}
