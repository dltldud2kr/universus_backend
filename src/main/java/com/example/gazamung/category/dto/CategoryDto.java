package com.example.gazamung.category.dto;

import com.example.gazamung.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryDto {
    private Long categoryId;
    private String categoryName;
    private List<CategoryDto> children;

    public static CategoryDto convertToDto(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
    public void setChildren(List<CategoryDto> children) {
        this.children = children;
    }


}