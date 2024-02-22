package com.example.gazamung.category.repository;

import com.example.gazamung.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c LEFT JOIN c.parent p ORDER BY p.categoryId ASC NULLS FIRST, c.categoryId ASC")
    List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc();


    boolean existsByCategoryCodeOrCategoryName(String categoryCode, String categoryName);


    List<Category> findByParent(Long categoryId);
}
