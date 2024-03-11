package com.example.gazamung.category.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.category.dto.CategoryDto;
import com.example.gazamung.category.service.CategoryService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;



    @PostMapping("/create")
    public ResultDTO createCategory(@RequestBody String categoryName){

        try {
            return ResultDTO.of(categoryService.create(categoryName), ApiResponseCode.SUCCESS.getCode(), "카테고리 생성", null);
        } catch (CustomException e) {
            return  ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }
    @Operation(summary = "카테고리 리스트", description = "" +
            "전체 카테고리 리스트를 반환합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 403: 회원정보 인증 실패" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
    })
    @GetMapping("/list")
    public ResultDTO getCategoryList() {
        try {
            List<CategoryDto> categoryList = categoryService.getCategoryList();
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "카테고리 리스트", categoryList);
        } catch (CustomException e) {
            return  ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @DeleteMapping("/delete")
    public ResultDTO deleteCategory(@RequestParam Long categoryId, Long memberIdx){
        try{
            return ResultDTO.of(categoryService.deleteCategory(categoryId, memberIdx), ApiResponseCode.SUCCESS.getCode(), "카테고리 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

}