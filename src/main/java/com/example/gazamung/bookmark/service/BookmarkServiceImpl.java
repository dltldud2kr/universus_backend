package com.example.gazamung.bookmark.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.bookmark.entity.Bookmark;
import com.example.gazamung.bookmark.repository.BookmarkRepository;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;

    @Override
    public boolean bookMark(Long memberIdx, Long clubId) {
//
//        if (!memberRepository.existsById(memberIdx)) {.
//            throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
//        }

        memberRepository.findById(memberIdx).orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));
        clubRepository.findById(clubId).orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));

        Optional<Bookmark> optionalBookmark = bookmarkRepository.findByMemberIdxAndClubId(memberIdx, clubId);
        if (optionalBookmark.isPresent()){
            bookmarkRepository.delete(optionalBookmark.get());
            return false;
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setMemberIdx(memberIdx);
        bookmark.setClubId(clubId);
        bookmarkRepository.save(bookmark);
        return true;
    }
}
