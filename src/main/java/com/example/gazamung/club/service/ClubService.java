package com.example.gazamung.club.service;


import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubJoinRequest;
import com.example.gazamung.club.dto.ClubRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 모임 가입
     * @param request
     * @return
     */

    boolean clubJoin(ClubJoinRequest request);

    List<ClubDto> list();

    ClubDto info(Long clubId);

    void secession(ClubJoinRequest request);
}
