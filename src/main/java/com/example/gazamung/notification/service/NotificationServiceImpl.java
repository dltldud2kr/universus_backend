package com.example.gazamung.notification.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.notification.entity.Notification;
import com.example.gazamung.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    public final NotificationRepository notificationRepository;
    public final MemberRepository memberRepository;
    @Override
    public void sendNotification(Long moimId, Long memberIdx) {
        Notification notification = Notification.builder()
                .createdAt(LocalDateTime.now())
                .content(notificationContent(memberIdx))
                .moimId(moimId)
                .memberIdx(memberIdx)
                .build();

        notificationRepository.save(notification);
    }

    private String notificationContent(Long memberIdx) {
        Optional<Member> memberOptional = memberRepository.findById(memberIdx);
        if (memberOptional.isPresent()){
            return memberOptional.get().getNickname() + "님으로부터 새로운 가입 요청이 도착했습니다.";
        } else throw new CustomException(CustomExceptionCode.NOT_FOUND);
    }

    @Override
    public List<Notification> getJoinRequest(Long moimId) {
        List<Notification> notificationList = notificationRepository.findAllByMoimId(moimId);
        return notificationList;
    }
}
