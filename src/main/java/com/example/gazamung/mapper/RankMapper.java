package com.example.gazamung.mapper;

import com.example.gazamung.rank.dto.UnivRankRes;
import com.example.gazamung.university.entity.University;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankMapper {

    void updateWinRank(@Param("winUniv") Long winUniv, @Param("eventId") Long eventId);

    void updateLoseRank(@Param("loseUniv") Long loseUniv, @Param("eventId") Long eventId);

    void insertRank(@Param("univId") Long univId, @Param("eventId") Long eventId,
                    @Param("rankPoint") Long rankPoint, @Param("winCount") Long winCount, @Param("loseCount") Long loseCount);

    int checkExistence(@Param("univId") Long univId, @Param("eventId") Long eventId);

    List<UnivRankRes> findRanksByEventId(Long eventId);
    List<UnivRankRes> findAllRanks();
}
