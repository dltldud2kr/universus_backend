package com.example.gazamung.university.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.department.entity.Department;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import com.example.gazamung.university.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/university")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대학교, 학과 SQL INSERT 쿼리문 API", description = "")
public class UniversityInsertController {

    private final DataSource dataSource;
    private final UniversityService universityService;
    private final UniversityRepository universityRepository;


    @Operation(summary = "대학, 학과 데이터 INSERT ", description = "" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })

    @PostMapping("/execute")
    public String executeSqlScript() throws IOException {
        // SQL 스크립트 파일 로드
        Resource universityScript = new ClassPathResource("university.sql");

        // 데이터베이스에 SQL 스크립트 실행
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(universityScript);
        populator.execute(dataSource);

        Resource departmentScript = new ClassPathResource("department.sql");

        // 데이터베이스에 SQL 스크립트 실행
        ResourceDatabasePopulator populator2 = new ResourceDatabasePopulator(departmentScript);
        populator2.execute(dataSource);
        return "SQL 스크립트가 성공적으로 실행되었습니다.";
    }

    @Operation(summary = "대학 리스트 조회 ", description = "" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })

    @GetMapping("/univList")
    public ResultDTO universityList() {

        try {
            List<University> list = universityService.universityList();
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "대학 리스트 조회 성공", list);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "대학 로고 UPDATE ", description = "" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })

    @PostMapping("/logoUpt")
    public String updateLogoImageUrl() {
        // 이전 이미지 URL의 프리픽스와 새로운 이미지 URL의 프리픽스
        String oldImagePrefix = "PNG_";
        String newImageUrlPrefix = "https://jhuniversus.s3.ap-northeast-2.amazonaws.com/logo/PNG_"; // 변경된 부분

        // University 엔티티 리스트 가져오기
        List<University> universities = universityRepository.findAll();

        // 각 University 엔티티의 로고 이미지 URL 업데이트
        for (University university : universities) {
            String logoImg = university.getLogoImg();
            if (logoImg != null && logoImg.startsWith(oldImagePrefix)) {
                university.setLogoImg(newImageUrlPrefix + logoImg.substring(oldImagePrefix.length()));
            }
        }
        // 변경된 내용 저장
        universityRepository.saveAll(universities);

        return "Successfully updated logo image URLs.";
    }


}