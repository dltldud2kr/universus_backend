package com.example.gazamung.club.service;


import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public interface ClubService {
    Map<String, Object> create(ClubRequest.CreateClubRequestDto dto);

    void update(ClubRequest.ModifyClubRequestDto dto);

    void delete(Long moimId, Long memberIdx);

    List<ClubDto> list();

    ClubDto info(Long clubId);
}
