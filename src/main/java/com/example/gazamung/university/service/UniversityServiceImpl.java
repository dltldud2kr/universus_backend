package com.example.gazamung.university.service;

import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
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


    /**
     * @Decription  '대학교' -> '대' 로 학교 이름을 줄여주는 메서드
     * ex) 가천대학교 ->  가천대
     * @param universities
     * @author 이시영
     * @return
     */
    public List<University> findAllWithoutWord(List<University> universities) {

        for (University university : universities) {
            String schoolName = university.getSchoolName();
            // "대학교"라는 단어를 제거
            schoolName = schoolName.replace("학교", "");
            // 공백을 제거하고 리스트에 다시 설정
            university.setSchoolName(schoolName.trim());
        }
        return universities;
    }

}
