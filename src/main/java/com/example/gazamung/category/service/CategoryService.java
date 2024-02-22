package com.example.gazamung.category.service;

import com.example.gazamung.category.dto.CategoryCreateRequest;
import com.example.gazamung.category.dto.CategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    boolean createCategory(CategoryCreateRequest req);

    List<CategoryDto> getCategoryList();

    boolean deleteCategory(Long categoryId, Long memberIdx);
}
