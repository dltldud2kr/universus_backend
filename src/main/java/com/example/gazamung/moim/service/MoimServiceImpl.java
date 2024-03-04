package com.example.gazamung.moim.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.category.entity.Category;
import com.example.gazamung.category.repository.CategoryRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.membership.entity.Membership;
import com.example.gazamung.membership.repository.MembershipRepository;
import com.example.gazamung.moim.controller.MoimController;
import com.example.gazamung.moim.dto.MoimDto;
import com.example.gazamung.moim.entity.Moim;
import com.example.gazamung.moim.repository.MoimRepository;
import com.example.gazamung.notification.entity.Notification;
import com.example.gazamung.notification.repository.NotificationRepository;
import com.example.gazamung.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoimServiceImpl implements MoimService {

    private final MoimRepository moimRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public boolean create(MoimDto dto) {
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        Moim moim = Moim.builder()
                .memberIdx(member.getMemberIdx())
                .title(dto.getTitle())
                .content(dto.getContent())
                .location(dto.getLocation())
                .categoryId(dto.getCategoryId())
                .regDt(LocalDateTime.now())
                .build();

        moimRepository.save(moim);
        return true;
    }

    private List<MoimDto> moimDtoList(List<Moim> list) {
        return list.stream()
                .map(moim -> {
                    return MoimDto.builder()
                            .moimId(moim.getMoimId())
                            .memberIdx(moim.getMemberIdx())
                            .title(moim.getTitle())
                            .content(moim.getContent())
                            .location(moim.getLocation())
                            .categoryId(moim.getCategoryId())
                            .likeCnt(moim.getLikeCnt())
                            .regDt(moim.getRegDt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<MoimDto> info(Long moimId) {
        List<Moim> list = moimRepository.findByMoimId(moimId);
        return moimDtoList(list);
    }

    public boolean delete(Long moimId, Long memberIdx) {
        Optional<Moim> moimOpt = moimRepository.findById(moimId);

        if (moimOpt.isPresent()) {
            Moim moim = moimOpt.get();

            if (moim.getMemberIdx().equals(memberIdx)) {
                moimRepository.delete(moim);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        }
        throw new CustomException(CustomExceptionCode.NOT_FOUND);

    }

    public boolean update(Long moimId, Long memberIdx, MoimDto dto) {
        Moim moim = moimRepository.findById(moimId)
                .filter(m -> m.getMemberIdx().equals(memberIdx))
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        moim.setTitle(dto.getTitle());
        moim.setContent(dto.getContent());
        moim.setLocation(dto.getLocation());
        moim.setCategoryId(dto.getCategoryId());
        moimRepository.save(moim);
        return true;
    }

    @Override
    public List<MoimDto> list() {
        List<Moim> list = moimRepository.findAll();
        return moimDtoList(list);
    }

    @Override
    public List<MoimDto> listCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            if (category.getParent() == null) { // parent가 null인 경우에만 실행
                List<Category> subCategories = category.getChildren();
                List<Moim> moimList = new ArrayList<>();
                for (Category subCategory : subCategories) {
                    Long subCategoryId = subCategory.getCategoryId();
                    moimList.addAll(moimRepository.findAllByCategoryId(subCategoryId));
                }
                return moimDtoList(moimList);
            } else { // parent가 null이 아닌 경우
                List<Moim> moimList = moimRepository.findAllByCategoryId(categoryId);
                return moimDtoList(moimList);
            }
        } else {
            return Collections.emptyList(); // 해당 categoryId를 가진 카테고리가 없는 경우 빈 리스트 반환
        }
    }

    @Override
    public List<MoimDto> listLikeCnt() {
        List<Moim> list = moimRepository.findAllOrderByLikeCntDesc();
        return moimDtoList(list);
    }

    @Override
    public List<MoimDto> search(String keyword, MoimController.SearchField searchField) {
        List<Moim> moims = null;

        switch (searchField) {
            case TITLE: // 제목
                moims = moimRepository.findByTitleContaining(keyword);
                break;
            case CONTENT:   // 내용
                moims = moimRepository.findByContentContaining(keyword);
                break;
            case TITLE_AND_CONTENT: // 제목 + 내용
                moims = moimRepository.findByTitleContainingOrContentContaining(keyword, keyword);
                break;
            case NICKNAME:  // 닉네임
//                 moims = moimRepository.findByNicknameContaining(keyword);
                break;
            default:
                moims = Collections.emptyList();
                break;
        }

        return convertToMoimDtoList(moims);
    }


    private MoimDto convertToMoimDto(Moim moim) {
        return MoimDto.builder()
                .moimId(moim.getMoimId())
                .memberIdx(moim.getMemberIdx())
                .title(moim.getTitle())
                .content(moim.getContent())
                .location(moim.getLocation())
                .categoryId(moim.getCategoryId())
                .likeCnt(moim.getLikeCnt())
                .regDt(moim.getRegDt())
                .build();
    }

    private List<MoimDto> convertToMoimDtoList(List<Moim> moimList) {
        return moimList.stream()
                .map(this::convertToMoimDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean joinRequest(Long moimId, Long memberIdx) {
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND)); // 모임이 존재하는지 확인

        if (moim.getMemberIdx().equals(memberIdx)) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND); // 모임장이 모임에 가입하려고 할 때 예외 처리
        }

        Long currentParticipants = membershipRepository.countByMoimIdAndStatus(moimId, MembershipStatus.APPROVED);

        if (currentParticipants >= moim.getMaximumParticipants()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND); // 모임의 최대 참가 인원 수 초과 예외 처리
        }

        // 가입 요청 처리
        membershipRepository.save(Membership.builder()
                .moimId(moimId)
                .memberIdx(memberIdx)
                .joinRequestDt(LocalDateTime.now())
                .status(MembershipStatus.PENDING) // 가입 요청 상태로 설정
                .build());

        notificationService.sendNotification(moimId, memberIdx);

        return true; // 가입 `요청 성공
    }

    private void processJoinRequest(Long memberIdx, Long notificationId, MembershipStatus status) {
        // Notification ID를 사용하여 Notification을 가져옵니다.
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_EMAIL));

        // Notification에서 Moim ID를 가져옵니다.
        Long moimId = notification.getMoimId();

        // Moim ID를 사용하여 Moim을 가져옵니다.
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.SERVER_ERROR));

        // Moim의 멤버 인덱스와 주어진 멤버 인덱스가 일치하는지 확인합니다.
        if (!moim.getMemberIdx().equals(memberIdx)) {
            throw new CustomException(CustomExceptionCode.DUPLICATED);
        }

        // 멤버 인덱스를 사용하여 해당 멤버십을 찾습니다.
        Membership membership = membershipRepository.findByMemberIdx(notification.getMemberIdx());

        if (status == MembershipStatus.APPROVED) {
            membership.setRegDt(LocalDateTime.now());
        } else if (status == MembershipStatus.REJECTED) {
            membership.setJoinRequestRejectedDt(LocalDateTime.now());
        }

        membership.setStatus(status);

        membershipRepository.save(membership);
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public void approveJoinRequest(Long memberIdx, Long notificationId) {
        processJoinRequest(memberIdx, notificationId, MembershipStatus.APPROVED);
    }

    @Override
    public void rejectJoinRequest(Long memberIdx, Long notificationId) {
        processJoinRequest(memberIdx, notificationId, MembershipStatus.REJECTED);
    }


    public enum MembershipStatus {
        APPROVED("승인됨"),
        PENDING("대기중"),
        REJECTED("거절됨");

        private final String description;

        MembershipStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

}

