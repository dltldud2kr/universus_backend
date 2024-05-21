package com.example.gazamung.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

@Mapper
public interface UnivBattleMapper {
    void updateRankPoints(@Param("winUniv") Long winUniv, @Param("loseUniv") Long loseUniv);

}
