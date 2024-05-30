package com.example.gazamung.event.repository;

import com.example.gazamung.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    String findEventNameByEventId(Long eventId);

    List<Event> findByEventNameContaining(String query);
}
