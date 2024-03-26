package com.example.gazamung.event.service;

import com.example.gazamung.event.dto.EventDto;
import com.example.gazamung.event.dto.ListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {
    boolean create(EventDto dto);

    boolean delete(EventDto dto);

    boolean update(EventDto dto);

    List<ListDto> list();
}
