package com.example.gazamung.group.service;


import com.example.gazamung.group.dto.GroupDto;
import org.springframework.stereotype.Service;



@Service
public interface GroupService {
    boolean create(GroupDto dto);


    boolean delete(Long moimId, Long memberIdx);







}
