package com.example.gazamung.rank.service;

import com.example.gazamung.rank.dto.DeptRankRes;
import com.example.gazamung.rank.dto.UnivRankRes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RankService {

    public List<UnivRankRes> univRankList(Long eventId);
    public List<DeptRankRes> deptRankList(Long eventId, Long univId);
}