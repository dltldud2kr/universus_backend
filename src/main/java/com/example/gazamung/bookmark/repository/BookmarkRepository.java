package com.example.gazamung.bookmark.repository;

import com.example.gazamung.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByMemberIdxAndClubId(Long memberIdx, Long clubId);

}
