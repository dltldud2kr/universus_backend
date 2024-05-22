package com.example.gazamung.rank.service;

import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.dto.NotifyRes;
import com.example.gazamung.rank.dto.UnivRankRes;
import com.example.gazamung.rank.entity.Rank;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RankService {

    public List<UnivRankRes> rankList(Long eventId);
}