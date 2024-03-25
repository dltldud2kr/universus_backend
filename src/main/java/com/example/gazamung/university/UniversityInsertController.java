package com.example.gazamung.university;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.university.service.UniversityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/university")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대학교 SQL INSERT 쿼리문 API", description = "")
public class UniversityInsertController {

    private final DataSource dataSource;
    private final UniversityService universityService;


    @PostMapping("/execute")
    public String executeSqlScript() throws IOException {
        // SQL 스크립트 파일 로드
        Resource scriptResource = new ClassPathResource("university.sql");

        // 데이터베이스에 SQL 스크립트 실행
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(scriptResource);
        populator.execute(dataSource);
        return "SQL 스크립트가 성공적으로 실행되었습니다.";
    }

    @GetMapping("/list")
    public ResultDTO universityList() {

        try{
            List<University> list  = universityService.universityList();
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"대학 리스트 조회 성공", list);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }
}
