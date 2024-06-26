package com.example.gazamung.club.service;


import com.example.gazamung.club.dto.*;
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


    List<ClubListDto> list(Long memberIdx);

    ClubDto info(Long clubId, Long memberIdx);

    void secession(ClubJoinRequest request);

    List<SuggestClub> suggest(Long memberIdx);

    void join(ClubJoinRequest request);

    List<MercenaryDto> mercenary(Long memberIdx);

    void fcmToken(String fcmToken, Long memberIdx);

    void expelMember(ExpelClub request);

    List<ClubListDto> joinedClubsList(Long memberIdx);

    List<ClubMembersDto> clubMembersList(Long memberIdx, Long clubId);
}
