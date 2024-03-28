package com.example.gazamung.department;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.department.entity.Department;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.university.entity.University;
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
@RequestMapping("/api/v1/department")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대학교, 학과 SQL INSERT 쿼리문 API", description = "")
public class DepartmentController {

    private final UniversityService universityService;

    @Operation(summary = "학과 리스트 조회 ", description = "" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/deptList")
    public ResultDTO allDepartmentList() {

        try{
            List<Department> list  = universityService.departmentList();
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"학과 리스트 조회 성공", list);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @GetMapping("/matchDept")
    public ResultDTO matchDeptList(@RequestParam Long univId) {
        try{
            List<Department> list  = universityService.matchDeptList(univId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"입력 대학 학과 리스트 조회 성공", list);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }
}
