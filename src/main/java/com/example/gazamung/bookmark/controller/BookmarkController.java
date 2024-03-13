package com.example.gazamung.bookmark.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.bookmark.service.BookmarkService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark")
    public ResultDTO bookMark(@RequestParam Long memberIdx, @RequestParam Long clubId) {
        try {
            boolean isBookmarked = bookmarkService.bookMark(memberIdx, clubId);
            return ResultDTO.of(isBookmarked, ApiResponseCode.CREATED.getCode(),
                    isBookmarked ? "북마크 설정 완료" : "북마크 해제 완료", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }
}
