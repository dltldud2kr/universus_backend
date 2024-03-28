package com.example.gazamung.department.entity;

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
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPARTMENT_SEQ")
    @SequenceGenerator(name = "DEPARTMENT_SEQ", sequenceName = "department_sequence", allocationSize = 1)
    private Long deptId;      // 학과 ID

    private Long univId;            // 학교명
    private String deptName;        // 학과명
}
