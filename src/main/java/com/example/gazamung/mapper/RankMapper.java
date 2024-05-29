package com.example.gazamung.mapper;

import com.example.gazamung.rank.dto.DeptRankRes;
import com.example.gazamung.rank.dto.UnivRankRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankMapper {

    /**
     * 대학 랭크 승리 업데이트
     * @param winUniv
     * @param eventId
     */
    void updateUnivWinRank(@Param("winUniv") Long winUniv, @Param("eventId") Long eventId);

    /**
     * 과 랭크 승리 업데이트
     * @param univId
     * @param winDept
     * @param eventId
     */
    void updateDeptWinRank(@Param("univId") Long univId, @Param("winDept") Long winDept, @Param("eventId") Long eventId);

    /**
     * 대학 랭크 패배 업데이트
     * @param loseUniv
     * @param eventId
     */
    void updateUnivLoseRank(@Param("loseUniv") Long loseUniv, @Param("eventId") Long eventId);

    /**
     * 과 랭크 패배 업데이트
     * @param loseDept
     * @param eventId
     */
    void updateDeptLoseRank(@Param("univId") Long univId , @Param("loseDept") Long loseDept, @Param("eventId") Long eventId);

    /**
     * 대학 랭크 INSERT
     * @param univId
     * @param eventId
     * @param rankPoint
     * @param winCount
     * @param loseCount
     */

    void insertUnivRank(@Param("univId") Long univId, @Param("eventId") Long eventId,
                        @Param("rankPoint") Long rankPoint, @Param("winCount") Long winCount, @Param("loseCount") Long loseCount);

    /**
     * 과 랭크 INSERT
     * @param univId
     * @param eventId
     * @param rankPoint
     * @param winCount
     * @param loseCount
     */
    void insertDeptRank(@Param("univId") Long univId, @Param("deptId") Long deptId, @Param("eventId") Long eventId,
                        @Param("rankPoint") Long rankPoint, @Param("winCount") Long winCount, @Param("loseCount") Long loseCount);

    int checkUnivExistence(@Param("univId") Long univId, @Param("eventId") Long eventId);

    int checkDeptExistence(@Param("univId") Long univId, @Param("deptId") Long deptId, @Param("eventId") Long eventId);

    List<UnivRankRes> findUnivRanksByEventId(Long eventId);
    List<UnivRankRes> findAllUnivRanks();

    List<DeptRankRes> findDeptRanksByEventId(Long eventId, Long univId);
    List<DeptRankRes> findAllDeptRanks(Long univId);


}
