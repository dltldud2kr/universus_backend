package com.example.gazamung.rank.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MsgType;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.mapper.RankMapper;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.dto.NotifyRes;
import com.example.gazamung.notification.entity.Notification;
import com.example.gazamung.notification.repository.NotificationRepository;
import com.example.gazamung.notification.service.NotificationService;
import com.example.gazamung.rank.dto.UnivRankRes;
import com.example.gazamung.rank.entity.Rank;
import com.example.gazamung.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;
    private final RankMapper rankMapper;

    @Override
    public List<UnivRankRes> rankList(Long eventId) {
        if (eventId != null) {
            return rankMapper.findRanksByEventId(eventId);
        } else {
            return rankMapper.findAllRanks();
        }

    }
}