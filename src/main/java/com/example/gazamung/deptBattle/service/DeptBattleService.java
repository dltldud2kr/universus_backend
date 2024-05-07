package com.example.gazamung.deptBattle.service;

import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.dto.DeptGuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import org.springframework.stereotype.Service;

@Service
public interface DeptBattleService {


    boolean create(DeptBattleCreateRequest request);

    boolean attend(DeptBattleAttendRequest request);

    boolean GuestLeaderAttend(DeptGuestLeaderAttendRequest request);

}
