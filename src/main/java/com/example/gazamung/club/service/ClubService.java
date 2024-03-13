package com.example.gazamung.club.service;


import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public interface ClubService {
    /**
     * 모임 생성
     * @param dto
     * @return
     */
    Map<String, Object> create(ClubRequest.CreateClubRequestDto dto);

    /**
     * 모임 수정
     * @param dto
     */
    void update(ClubRequest.ModifyClubRequestDto dto);

    /**
     * 모임 삭제
     * @param moimId
     * @param memberIdx
     */

    void delete(Long moimId, Long memberIdx);

    List<ClubDto> list();

    ClubDto info(Long clubId);
}
