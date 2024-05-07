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


    public Object infoPost(Long univBoardId) {
        UnivBoard post = univBoardRepository.findById(univBoardId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        Category category = categoryRepository.findById(post.getCategoryId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CATEGORY));

        Club club = null;
        if (post.getClubId() != null) {
            club = clubRepository.findById(post.getClubId())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));
        }

        List<UploadImage> postImage = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoardId);
        List<String> postImageUrls = new ArrayList<>();
        for (UploadImage image : postImage) {
            postImageUrls.add(image.getImageUrl());
        }

        InfoPost.InfoPostBuilder builder = InfoPost.builder()
                .memberIdx(post.getMemberIdx())
                .clubName(club != null ? club.getClubName() : null)
                .categoryName(category.getCategoryName())
                .title(post.getTitle())
                .content(post.getContent())
                .regDt(post.getRegDt())
                .postImageUrls(postImageUrls);

        // categoryId가 1인 경우, 위치 정보도 반환 객체에 포함
        if (post.getCategoryId() == 1) {
            builder.lat(post.getLat())
                    .lng(post.getLng())
                    .place(post.getPlace());
        }

        return builder.build();
    }


    /**
     * @param dto
     * @title 게시글 작성
     * @created 24.05.06 이승열
     * @description 커뮤니티 게시글 작성 (학교 게시판, 모임 게시판 공통 사용)
     */
    @Override
    public Map<String, Object> createPost(PostDto dto) {
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
        categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CATEGORY));

        UnivBoard univBoard;
        // clubId 유무로 학교 / 모임 게시판 구분
        if (dto.getClubId() == null) {
            clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));
            univBoard = UnivBoard.builder()
                    .memberIdx(dto.getMemberIdx())
                    .categoryId(0L) // 잠재적으로 이 부분도 개선이 필요할 수 있습니다.
                    .univId(member.getUnivId())
                    .deptId(member.getDeptId())
                    .clubId(dto.getClubId())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .regDt(LocalDateTime.now())
                    .build();
        } else {
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
            // categoryId == 1 인 경우 위치 정보도 저장
            if (dto.getCategoryId() == 1) {
                univBoard.setLat(dto.getLat());
                univBoard.setLng(dto.getLng());
                univBoard.setPlace(dto.getPlace());
                System.out.println("Latitude set to: " + dto.getLat());
                System.out.println("Longitude set to: " + dto.getLng());
                System.out.println("Place set to: " + dto.getPlace());
            }
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
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 게시글 목록 조회
        List<UnivBoard> univBoards = (clubId == null) ? univBoardRepository.findByClubIdIsNullAndUnivId(member.getUnivId())
                : univBoardRepository.findByClubIdAndUnivId(clubId, member.getUnivId());

        List<InfoPost> infoPosts = new ArrayList<>();
        for (UnivBoard univBoard : univBoards) {
            // 카테고리 정보 조회
            Category category = categoryRepository.findById(univBoard.getCategoryId())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CATEGORY));

            // 클럽 정보 조회, 클럽 ID가 null이면 조회하지 않음
            Club club = null;
            if (univBoard.getClubId() != null) {
                club = clubRepository.findById(univBoard.getClubId())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));
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






