package com.example.gazamung.deptBattle.repository;

import com.example.gazamung.ChatRoom.ChatRoom;
import com.example.gazamung.ChatRoom.ChatRoomRepository;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.deptBattle.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.DeptBattleService;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.univBattle.dto.UnivBattleAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.repository.UnivBattleRepository;
import com.example.gazamung.univBattle.service.UnivBattleService;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptBattleServiceImpl implements DeptBattleService {

    private final UniversityRepository universityRepository;
    private final DeptBattleRepository deptBattleRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    @Override
    public boolean create(DeptBattleCreateRequest request) {


        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        long univId = member.getUnivId();

        DeptBattle deptBattle = DeptBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .univId(univId)
                .battleDate(request.getBattleDate())
                .location(request.getLocation())
                .content(request.getContent())
                .status(0)
                .cost(request.getCost())
                .regDt(LocalDateTime.now())
                .build();

        deptBattleRepository.save(deptBattle);



        return false;
    }

    @Override
    public boolean attend(DeptBattleAttendRequest request) {
        return false;
    }
}
