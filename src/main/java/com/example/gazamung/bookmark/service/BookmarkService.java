package com.example.gazamung.bookmark.service;

import org.springframework.stereotype.Service;

@Service
public interface BookmarkService {
    boolean bookMark(Long memberIdx, Long clubId);
}
