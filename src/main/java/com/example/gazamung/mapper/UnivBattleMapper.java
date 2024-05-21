package com.example.gazamung.mapper;

import com.example.gazamung.univBattle.entity.UnivBattle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface UnivBattleMapper {
//    void updateRankPoints(@Param("winUniv") Long winUniv, @Param("loseUniv") Long loseUniv);

    List<UnivBattle> findByUnivId(@Param("univId") Long univId);
}
