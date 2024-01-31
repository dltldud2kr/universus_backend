package com.example.gazamung.message;

import com.example.gazamung.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {


}

