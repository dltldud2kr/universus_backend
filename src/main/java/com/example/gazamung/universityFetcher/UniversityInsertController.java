package com.example.gazamung.universityFetcher;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/university")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대학교 SQL INSERT 쿼리문 API", description = "")
public class UniversityInsertController {

    private final DataSource dataSource;


    @PostMapping("/execute")
    public String executeSqlScript() throws IOException {
        // SQL 스크립트 파일 로드
        Resource scriptResource = new ClassPathResource("university.sql");

        // 데이터베이스에 SQL 스크립트 실행
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(scriptResource);
        populator.execute(dataSource);
        return "SQL 스크립트가 성공적으로 실행되었습니다.";
    }
}
