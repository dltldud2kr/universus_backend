package com.example.gazamung.deptBattle;

import com.example.gazamung.univBattle.dto.UnivBattleAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import org.springframework.stereotype.Service;

@Service
public interface DeptBattleService {


    boolean create(DeptBattleCreateRequest request);

    boolean attend(DeptBattleAttendRequest request);
}
