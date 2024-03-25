package com.example.gazamung.univBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.univBattle.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.repository.UnivBattleRepository;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnivBattleServiceImpl implements UnivBattleService {

    private final UniversityRepository universityRepository;
    private final UnivBattleRepository univBattleRepository;
    private final MemberRepository memberRepository;


    @Override
    public boolean create(UnivBattleCreateRequest request) {

        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        long univId = member.getUnivId();


        UnivBattle univBattle = UnivBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .hostUniv(univId)
                .battleDate(request.getBattleDate())
                .location(request.getLocation())
                .status(0)
                .regDt(LocalDateTime.now())
                .build();

        univBattleRepository.save(univBattle);

        return true;

    }
}
