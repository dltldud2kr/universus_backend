package com.example.gazamung.mapper;

import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.university.entity.University;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UniversityMapper {
    List<University> findAllOrderByRankPointDesc();
}
