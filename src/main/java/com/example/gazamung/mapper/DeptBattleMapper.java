package com.example.gazamung.mapper;

import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.univBattle.entity.UnivBattle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptBattleMapper {

    List<DeptBattle> findByDeptAndUnivId( @Param("univId") Long univId, @Param("deptId") Long deptId);
}
