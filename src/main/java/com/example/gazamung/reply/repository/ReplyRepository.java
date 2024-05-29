package com.example.gazamung.reply.repository;

import com.example.gazamung.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByUnivBoardId(Long univBoardId);

    List<Reply> findByUnivBoardIdOrderByLastDtAsc(Long univBoardId);
}
