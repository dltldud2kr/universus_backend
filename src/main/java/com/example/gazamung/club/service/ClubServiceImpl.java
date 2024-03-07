package com.example.gazamung.club.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public boolean create(ClubDto dto) {
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));


        Club club = Club.builder()
                .memberIdx(member.getMemberIdx())
                .title(dto.getTitle())
                .content(dto.getContent())
                .location(dto.getLocation())
                .categoryId(dto.getCategoryId())
                .regDt(LocalDateTime.now())
                .build();

        clubRepository.save(club);
        return true;
    }

    public boolean delete(Long clubId, Long memberIdx) {
        Optional<Club> clubOpt = clubRepository.findById(clubId);

        if (clubOpt.isPresent()) {
            Club club = clubOpt.get();

            //모임장과 삭제 요청 회원이 동일한지 확인.
            if (club.getMemberIdx().equals(memberIdx)) {
                clubRepository.delete(club);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        }
        throw new CustomException(CustomExceptionCode.NOT_FOUND);

    }





}

