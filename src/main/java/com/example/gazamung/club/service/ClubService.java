package com.example.gazamung.club.service;


import com.example.gazamung.club.dto.ClubDto;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface ClubService {
    boolean create(ClubDto dto);


    boolean delete(Long moimId, Long memberIdx);

    List<ClubDto> list();

    ClubDto info(Long clubId);
}
