package com.example.gazamung.category.service;

import com.example.gazamung.category.dto.CategoryDto;
import com.example.gazamung.category.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    boolean create(String categoryName, Long memberIdx);
    boolean deleteCategory(Long categoryId, Long memberIdx);

    List<CategoryDto> getCategoryList();
}
