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
import org.springframework.transaction.annotation.Transactional;

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


    /**
     * @param univBoardId
     * @title 게시글 조회
     * @created 24.05.07 이승열
     * @description 커뮤니티 게시글 조회
     */
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

        // 멤버 조회
        Member member = memberRepository.findById(post.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        List<UploadImage> postImage = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoardId);
        List<String> postImageUrls = new ArrayList<>();
        for (UploadImage image : postImage) {
            postImageUrls.add(image.getImageUrl());
        }

        String nickOrAnon = post.getAnonymous() == 1 ? "익명" : member.getNickname();

        InfoPost.InfoPostBuilder builder = InfoPost.builder()
                .clubName(club != null ? club.getClubName() : null)
                .nickOrAnon(nickOrAnon)
                .categoryName(category.getCategoryName())
                .title(post.getTitle())
                .content(post.getContent())
                .regDt(post.getRegDt())
                .postImageUrls(postImageUrls)
                .profileImgUrl(post.getAnonymous() == 1 ?
                        "https://jhuniversus.s3.ap-northeast-2.amazonaws.com/default/df_profile.jpg" :
                        member.getProfileImgUrl())
                .univBoardId(post.getUnivBoardId())
                .memberIdx(post.getMemberIdx()); // 지우지마세요! 지우면 캡스톤 다지움

        // categoryId가 1인 경우, 위치 정보도 반환 객체에 포함
        if (post.getCategoryId() == 1) {
            builder.lat(post.getLat())
                    .lng(post.getLng())
                    .place(post.getPlace())
                    .matchDt(post.getMatchDt());
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
        if (dto.getClubId() != null) {
            clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));
            univBoard = UnivBoard.builder()
                    .memberIdx(dto.getMemberIdx())
                    .categoryId(1L) // 잠재적으로 이 부분도 개선이 필요할 수 있습니다.
                    .univId(member.getUnivId())
                    .deptId(member.getDeptId())
                    .clubId(dto.getClubId())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .regDt(LocalDateTime.now())
                    .eventId(dto.getEventId())
                    .anonymous(dto.getAnonymous())
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
                    .eventId(dto.getEventId())
                    .anonymous(dto.getAnonymous())
                    .build();
            // categoryId == 1 인 경우 위치 정보도 저장
            if (dto.getCategoryId() == 1) {
                univBoard.setLat(dto.getLat());
                univBoard.setLng(dto.getLng());
                univBoard.setPlace(dto.getPlace());
                univBoard.setMatchDt(dto.getMatchDt());
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

    /**
     * @param memberIdx
     * @param clubId
     * @title 게시글 리스트 조회
     * @created 24.05.07 이승열
     * @description 커뮤니티 게시글 리스트 조회
     */
    @Override
    public List<InfoPost> listPost(Long memberIdx, Long clubId, Long categoryId) {

        // 멤버 조회
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 카테고리 별 게시글 목록 조회
        List<UnivBoard> univBoards;
        if (categoryId != null && categoryId == 0) { // 카테고리가 0이면 전체 게시글 조회
            univBoards = (clubId == null) ? univBoardRepository.findByClubIdIsNullAndUnivIdOrderByRegDtAsc(member.getUnivId())
                    : univBoardRepository.findByClubIdAndUnivIdOrderByRegDtAsc(clubId, member.getUnivId());
        } else { // 특정 카테고리의 리스트만 반환
            univBoards = (clubId == null) ? univBoardRepository.findByClubIdIsNullAndUnivIdAndCategoryIdOrderByRegDtAsc(member.getUnivId(), categoryId)
                    : univBoardRepository.findByClubIdAndUnivIdAndCategoryIdOrderByRegDtAsc(clubId, member.getUnivId(), categoryId);
        }

        List<InfoPost> infoPosts = new ArrayList<>();
        for (UnivBoard univBoard : univBoards) {
            // 카테고리 정보 조회
            Category category = null;
            if (categoryId != null) {
                category = categoryRepository.findById(univBoard.getCategoryId())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CATEGORY));
            }

            // 클럽 정보 조회, 클럽 ID가 null이면 조회하지 않음
            Club club = null;
            if (univBoard.getClubId() != null) {
                club = clubRepository.findById(univBoard.getClubId())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));
            }

            // 작성자 닉네임, 프로필이미지 반환
            Member PostMember = memberRepository.findById(univBoard.getMemberIdx()).orElse(null);

            // 게시글의 이미지 정보 조회
            List<UploadImage> postImages = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoard.getUnivBoardId());

            String nickOrAnon = univBoard.getAnonymous() == 1 ? "익명" : member.getNickname();

            // 게시글의 상세 정보 생성
            InfoPost infoPost = InfoPost.builder()
                    .univBoardId(univBoard.getUnivBoardId())
                    .nickOrAnon(nickOrAnon)
                    .clubName(club != null ? club.getClubName() : null)
                    .categoryName(category.getCategoryName())
                    .title(univBoard.getTitle())
                    .content(univBoard.getContent())
                    .regDt(univBoard.getRegDt())
                    .postImageUrls(postImages.stream().map(UploadImage::getImageUrl).collect(Collectors.toList()))
                    .profileImgUrl(univBoard.getAnonymous() == 1 ?
                            "https://jhuniversus.s3.ap-northeast-2.amazonaws.com/default/df_profile.jpg" :
                            member.getProfileImgUrl())
                    .build();

            infoPosts.add(infoPost);
        }

        return infoPosts;
    }

    /**
     * @param univBoardId
     * @param memberIdx
     * @title 게시글 삭제
     * @created 24.05.08 이승열
     * @description 커뮤니티 게시글 삭제
     */
    @Override
    @Transactional
    public void deletePost(Long univBoardId, Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        UnivBoard univBoard = univBoardRepository.findById(univBoardId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        // 글 작성자이거나 관리자인 경우
        if ((univBoard.getMemberIdx().equals(memberIdx)) || (member.getRole() == 1)) {
            univBoardRepository.delete(univBoard);

            List<UploadImage> imageByAttachmentType = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoardId);
            String[] removeTarget = new String[imageByAttachmentType.size() + 1];

            int removeCount = 0;
            //업로드된 이미지가 잇는 경우
            try {
                if (imageByAttachmentType.size() > 0) {
                    for (UploadImage file : imageByAttachmentType) {
                        // 문자열에서 ".com/" 다음의 정보를 추출
                        int startIndex = file.getImageUrl().indexOf(".com/") + 5;
                        String result = file.getImageUrl().substring(startIndex);
                        removeTarget[removeCount] = result;
                        removeCount++;
                    }
                    //등록되어있는 파일 정보 삭제 요청.
                    uploadService.removeS3Files(removeTarget);
                    //데이터베이스에 맵핑되어있는 정보삭제
                    uploadService.removeDatabaseByReviewIdx(univBoardId);
                }
            } catch (CustomException e) {
                throw new CustomException(CustomExceptionCode.SERVER_ERROR);
            }
        } else {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

    }

    @Override
    public void modifyPost(PostDto dto) {
        try {
            UnivBoard univBoard = univBoardRepository.findById(dto.getUnivBoardId())
                    .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

            memberRepository.findById(dto.getMemberIdx())
                    .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            //수정을 요청한 사용자와 작성자가 다른 경우 : (본인인지의 대한 유효성 검사)
            if (dto.getMemberIdx() != univBoard.getMemberIdx()) {
                throw new CustomException(CustomExceptionCode.ACCESS_DENIED);
            }
            if (dto.getPostImage() != null && !dto.getPostImage().isEmpty()) {
                //유효성 검증에 모두 통과했다면 버킷에 업로드되어있는 CLUB 파일을 모두 삭제합니다.
                //해당 모임에 업로드 등록되어있는 이미지를 검색합니다.
                List<UploadImage> imageByAttachmentType = uploadService.getImageByAttachmentType(AttachmentType.POST, dto.getUnivBoardId());
                String[] removeTarget = new String[imageByAttachmentType.size() + 1];

                int removeCount = 0;

                //업로드된 이미지가 잇는 경우
                try {
                    if (imageByAttachmentType.size() > 0) {
                        for (UploadImage file : imageByAttachmentType) {
                            // 문자열에서 ".com/" 다음의 정보를 추출
                            int startIndex = file.getImageUrl().indexOf(".com/") + 5;
                            String result = file.getImageUrl().substring(startIndex);
                            removeTarget[removeCount] = result;
                            removeCount++;
                        }
                        //등록되어있는 파일 정보 삭제 요청.
                        uploadService.removeS3Files(removeTarget);
                        //데이터베이스에 맵핑되어있는 정보삭제
                        uploadService.removeDatabaseByReviewIdx(dto.getUnivBoardId());
                    }
                } catch (CustomException e) {
                    throw new CustomException(CustomExceptionCode.SERVER_ERROR);
                }

                //새롭게 요청온 업로드 이미지를  버킷에 업로드함.
                uploadService.upload(dto.getPostImage(), dto.getMemberIdx(), AttachmentType.POST, univBoard.getUnivBoardId());

                //업로드된 이미지 정보를 데이터베이스
                List<UploadImage> getRepresentIdx = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoard.getUnivBoardId());
            }

            univBoard.setTitle(dto.getTitle());
            univBoard.setContent(dto.getContent());
            univBoard.setMatchDt(dto.getMatchDt());
            univBoard.setCategoryId(dto.getCategoryId());
            univBoard.setUdtDt(LocalDateTime.now());
            univBoard.setAnonymous(dto.getAnonymous());
            if (dto.getClubId() != null)
                univBoard.setClubId(dto.getClubId());
            if (dto.getCategoryId() == 1L) {
                univBoard.setLat(dto.getLat());
                univBoard.setLng(dto.getLng());
                univBoard.setPlace(dto.getPlace());
                univBoard.setMatchDt(dto.getMatchDt());
                univBoard.setEventId(dto.getEventId());
            }

            univBoardRepository.save(univBoard);
        } catch (CustomException e) {
            throw e;
        }

    }

}







