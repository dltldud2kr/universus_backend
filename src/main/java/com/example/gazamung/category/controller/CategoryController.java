package com.example.gazamung.category.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.category.dto.CategoryCreateRequest;
import com.example.gazamung.category.dto.CategoryDto;
import com.example.gazamung.category.service.CategoryService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
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
    public ResultDTO createCategory(@RequestBody CategoryCreateRequest req){
        try{
            return ResultDTO.of(categoryService.createCategory(req), ApiResponseCode.CREATED.getCode(),"카테고리 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/list")
    public List getCategoryList() {
        try {
            List<CategoryDto> categoryList = categoryService.getCategoryList();
            return categoryList;
        } catch (CustomException e) {
            return (List) ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @DeleteMapping("/delete")   //카테고리 삭제
    public ResultDTO deleteCategory(@RequestParam Long categoryId, Long memberIdx){
        try{
            return ResultDTO.of(categoryService.deleteCategory(categoryId, memberIdx), ApiResponseCode.SUCCESS.getCode(), "카테고리 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

}