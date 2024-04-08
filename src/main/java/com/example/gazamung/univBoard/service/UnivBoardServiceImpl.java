package com.example.gazamung.univBoard.service;

import com.example.gazamung.S3FileUploader.UploadService;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.univBoard.dto.UnivBoardDto;
import com.example.gazamung.univBoard.entity.UnivBoard;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    @Override
    public Map<String, Object> createPost(UnivBoardDto.CreateUnivBoardDto dto) {

        // 사용자 존재 여부 판단
        memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        UnivBoard univBoard = UnivBoard.builder()
                .memberIdx(dto.getMemberIdx())
                .categoryId(dto.getCategoryId())
                .univId(dto.getUnivId())
                .deptId(dto.getDeptId())
                .clubId(dto.getClubId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .regDt(LocalDateTime.now())
                .build();

        UnivBoard savedUnivBoard = univBoardRepository.save(univBoard);

        List<Map<String, Object>> uploadedImages = null;

        if (dto.getPostImage() != null && !dto.getPostImage().isEmpty()) {
            //정상적으로 모임이 생성됐는 경우 리뷰에 등록할 이미지를 첨부했는지 확인하고 해당 리뷰 IDX에 이미지 업로드를 실행함
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
    public Object infoPost(Long univBoardId) {
        UnivBoard post = univBoardRepository.findById(univBoardId)
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND));

        // 게시글 정보를 Dto로 변환하여 반환
        return UnivBoardDto.InfoUnivBoardDto.builder()
                .univBoardId(univBoardId)
                .memberIdx(post.getMemberIdx())
                .categoryId(post.getCategoryId())
                .univId(post.getUnivId())
                .deptId(post.getDeptId())
                .clubId(post.getClubId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImage(uploadService.getImageByAttachmentType(AttachmentType.POST, univBoardId))
                .regDt(LocalDateTime.now())
                .build();
    }

    @Override
    public List<UnivBoardDto.InfoUnivBoardDto> listUniv(Long memberIdx) {
        // 멤버 조회
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 대학 ID와 일치하는 게시글 목록 조회
        List<UnivBoard> univBoardList = univBoardRepository.findByUnivId(member.getUnivId());

        // 게시글 정보를 Dto로 변환하여 반환
        return univBoardList.stream()
                .map(post -> UnivBoardDto.InfoUnivBoardDto.builder()
                        .univBoardId(post.getUnivBoardId())
                        .memberIdx(post.getMemberIdx())
                        .categoryId(post.getCategoryId())
                        .univId(post.getUnivId())
                        .deptId(post.getDeptId())
                        .clubId(post.getClubId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .postImage(uploadService.getImageByAttachmentType(AttachmentType.POST, post.getUnivBoardId()))
                        .regDt(post.getRegDt())
                        .build())
                .collect(Collectors.toList());
    }




}
