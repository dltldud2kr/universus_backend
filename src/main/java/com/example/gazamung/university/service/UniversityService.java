package com.example.gazamung.university.service;

import com.example.gazamung.university.entity.University;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UniversityService {

    List<University> universityList();
}
