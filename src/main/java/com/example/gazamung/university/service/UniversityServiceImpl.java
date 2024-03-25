package com.example.gazamung.university.service;

import com.example.gazamung.university.University;
import com.example.gazamung.university.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityServiceImpl implements  UniversityService{

    private final UniversityRepository universityRepository;


    @Override
    public List<University> universityList() {

        List<University> list = universityRepository.findAll();

        return list;
    }
}
