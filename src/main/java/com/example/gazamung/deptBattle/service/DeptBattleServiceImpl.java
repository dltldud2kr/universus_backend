package com.example.gazamung.deptBattle.service;

import com.example.gazamung.ChatRoom.ChatRoomRepository;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.Status;
import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.repository.DeptBattleRepository;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                .status(Status.WAITING)
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
