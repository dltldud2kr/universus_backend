package com.example.gazamung.club.service;


import com.example.gazamung.club.dto.ClubDto;
import org.springframework.stereotype.Service;



@Service
public interface ClubService {
    boolean create(ClubDto dto);


    boolean delete(Long moimId, Long memberIdx);







}
