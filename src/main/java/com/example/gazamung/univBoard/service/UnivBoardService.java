package com.example.gazamung.univBoard.service;

import com.example.gazamung.univBoard.dto.InfoPost;
import com.example.gazamung.univBoard.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UnivBoardService {

    Object infoPost(Long univBoardId);

    Map<String, Object> createPost(PostDto dto);

    List<InfoPost> listPost(Long memberIdx, Long clubId, Long categoryId);

    void deletePost(Long univBoardId, Long memberIdx);

    void modifyPost(PostDto dto);

    boolean deletePostAdmin(Long univBoardId);
}