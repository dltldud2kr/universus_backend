package com.example.gazamung.univBoard.service;

import com.example.gazamung.S3FileUploader.UploadImage;
import com.example.gazamung.S3FileUploader.UploadService;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.category.entity.Category;
import com.example.gazamung.category.repository.CategoryRepository;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.univBoard.dto.InfoPost;
import com.example.gazamung.univBoard.dto.PostDto;
import com.example.gazamung.univBoard.entity.UnivBoard;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnivBoardServiceImpl implements UnivBoardService {

    private final MemberRepository memberRepository;
    private final UnivBoardRepository univBoardRepository;
    private final UploadService uploadService;
    private final CategoryRepository categoryRepository;
    private final ClubRepository clubRepository;


    @Override
    public Object infoPost(Long univBoardId) {
        UnivBoard post = univBoardRepository.findById(univBoardId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        Category category = categoryRepository.findById(post.getCategoryId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        Club club = clubRepository.findById(post.getClubId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        List<UploadImage> postImage = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoardId);


        List<String> postImageUrls = new ArrayList<>();
        for (UploadImage image : postImage) {
            postImageUrls.add(image.getImageUrl());
        }

        return InfoPost.builder()
                .memberIdx(post.getMemberIdx())
                .clubName(club.getClubName())
                .categoryName(category.getCategoryName())
                .title(post.getTitle())
                .content(post.getContent())
                .regDt(post.getRegDt())
                .postImageUrls(postImageUrls)
                .build();
    }

    @Override
    public Map<String, Object> createPost(PostDto dto) {
        // 사용자 존재 여부 판단
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
        // 카테고리 존재 여부 판단
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        UnivBoard univBoard;
        //  모임 게시판
        if (dto.getClubId() != null) {
            // 모임 존재 여부 판단
            clubRepository.findById(dto.getClubId())
                    .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND));
            univBoard = UnivBoard.builder()
                    .memberIdx(dto.getMemberIdx())
                    .categoryId(0L)
                    .univId(member.getUnivId())
                    .deptId(member.getDeptId())
                    .clubId(dto.getClubId())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .regDt(LocalDateTime.now())
                    .build();
        } else {
            // 대학 게시판
            univBoard = UnivBoard.builder()
                    .memberIdx(dto.getMemberIdx())
                    .categoryId(dto.getCategoryId())
                    .univId(member.getUnivId())
                    .deptId(member.getDeptId())
                    .clubId(null)
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .regDt(LocalDateTime.now())
                    .build();
        }
        UnivBoard savedUnivBoard = univBoardRepository.save(univBoard);

        List<Map<String, Object>> uploadedImages = null;

        if (dto.getPostImage() != null && !dto.getPostImage().isEmpty()) {
            uploadedImages = uploadService.upload(dto.getPostImage(), dto.getMemberIdx(), AttachmentType.POST, savedUnivBoard.getUnivBoardId());

            // 업로드된 이미지 중 0번째 이미지를 대표 이미지로 지정
            Long representIdx = null;
            if (!uploadedImages.isEmpty()) {
                representIdx = (Long) uploadedImages.get(0).get("idx");
                univBoard.setRepresentIdx(representIdx);
                univBoardRepository.save(savedUnivBoard); // 대표 이미지 설정 후 다시 저장
            }
        }
        // uploadImages 와 Post 정보를 함께 반환
        Map<String, Object> result = new HashMap<>();
        result.put("uploadedImages", uploadedImages);
        result.put("post", univBoard);

        return result;
    }

    @Override
    public List<InfoPost> listPost(Long memberIdx, Long clubId) {

        // 멤버 조회
        memberRepository.findById(memberIdx).orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 게시글 목록 조회
        List<UnivBoard> univBoards = (clubId == null) ? univBoardRepository.findByClubIdIsNull()
                : univBoardRepository.findByClubId(clubId);

        List<InfoPost> infoPosts = new ArrayList<>();
        for (UnivBoard univBoard : univBoards) {
            // 카테고리 정보 조회
            Category category = categoryRepository.findById(univBoard.getCategoryId())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

            // 클럽 정보 조회, 클럽 ID가 null이면 조회하지 않음
            Club club = null;
            if (univBoard.getClubId() != null) {
                club = clubRepository.findById(univBoard.getClubId())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));
            }

            // 게시글의 이미지 정보 조회
            List<UploadImage> postImages = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoard.getUnivBoardId());

            // 게시글의 상세 정보 생성
            InfoPost infoPost = InfoPost.builder()
                    .memberIdx(univBoard.getMemberIdx())
                    .clubName(club != null ? club.getClubName() : null)
                    .categoryName(category.getCategoryName())
                    .title(univBoard.getTitle())
                    .content(univBoard.getContent())
                    .regDt(univBoard.getRegDt())
                    .postImageUrls(postImages.stream().map(UploadImage::getImageUrl).collect(Collectors.toList()))
                    .build();

            infoPosts.add(infoPost);
        }

        return infoPosts;
    }


}






