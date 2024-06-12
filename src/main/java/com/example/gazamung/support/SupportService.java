package com.example.gazamung.support;

import com.example.gazamung.announcement.Announcement;
import com.example.gazamung.announcement.AnnouncementCreateReq;
import com.example.gazamung.announcement.AnnouncementRepository;
import com.example.gazamung.announcement.AnnouncementUpdateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SupportService {

    private final SupportRepository supportRepository;

    public boolean create(SupportCreateReq req){

        Support support = Support.builder()

                .memberIdx(req.getMemberIdx())
                .title(req.getTitle())
                .content(req.getContent())
                .regDt(LocalDateTime.now())
                .build();

        supportRepository.save(support);

        return true;
    }


    public Support read(Long supportId) {
        Optional<Support> supportOptional = supportRepository.findById(supportId);
        return supportOptional.orElse(null);
    }



    public boolean update(Long supportId, SupportUpdateReq req) {
        Optional<Support> supportOptional = supportRepository.findById(supportId);

        if (supportOptional.isPresent()) {
            Support support = supportOptional.get();
            support.setTitle(req.getTitle());
            support.setContent(req.getContent());
            support.setUdtDt(LocalDateTime.now());
            supportRepository.save(support);
            return true;
        } else {
            return false; // 해당 ID의 공지사항이 존재하지 않음
        }
    }

    public boolean delete(Long supportId) {
        Optional<Support> supportOptional = supportRepository.findById(supportId);

        if (supportOptional.isPresent()) {
            supportRepository.deleteById(supportId);
            return true;
        } else {
            return false; // 해당 ID의 공지사항이 존재하지 않음
        }
    }


}
