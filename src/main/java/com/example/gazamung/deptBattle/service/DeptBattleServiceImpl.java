package com.example.gazamung.deptBattle.service;

import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptBattleServiceImpl implements DeptBattleService {


    @Override
    public boolean create(DeptBattleCreateRequest request) {




        return false;
    }

    @Override
    public boolean attend(DeptBattleAttendRequest request) {
        return false;
    }
}
