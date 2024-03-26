package com.example.gazamung.event.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.event.dto.EventDto;
import com.example.gazamung.event.dto.ListDto;
import com.example.gazamung.event.entity.Event;
import com.example.gazamung.event.repository.EventRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @Override
    public boolean create(EventDto dto) {
        checkAdmin(dto.getMemberIdx()); // 관리자 여부 확인

        eventRepository.save(Event.builder().eventName(dto.getEventName()).build());

        return true;
    }

    @Override
    public boolean delete(EventDto dto) {
        checkAdmin(dto.getMemberIdx()); // 관리자 여부 확인

        eventRepository.deleteById(dto.getEventId());

        return true;
    }

    @Override
    public boolean update(EventDto dto) {
        checkAdmin(dto.getMemberIdx()); // 관리자 여부 확인

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));

        event.setEventName(dto.getEventName());

        eventRepository.save(event);

        return true;
    }

    @Override
    public List<ListDto> list() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(event -> ListDto.builder()
                        .eventId(event.getEventId())
                        .eventName(event.getEventName())
                        .build())
                .collect(Collectors.toList());
    }


    // 관리자 여부 확인 메서드
    private void checkAdmin(Long memberIdx) {
        Member member = memberRepository.findByMemberIdx(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        // 해당 회원이 관리자인지 판별
        if (member.getRole() != 1) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }
    }
}

