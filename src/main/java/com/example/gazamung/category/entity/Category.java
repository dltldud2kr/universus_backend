package com.example.gazamung.category.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ")
    @SequenceGenerator(name = "CATEGORY_SEQ", sequenceName = "category_sequence", allocationSize = 1)
    private Long categoryId;

    private String categoryName;    // 내 학과, 내 학교 모집, 학과, 클럽 게시판


}
