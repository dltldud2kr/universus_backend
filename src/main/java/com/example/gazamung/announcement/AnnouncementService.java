package com.example.gazamung.announcement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public boolean create(AnnouncementCreateReq req){

        Announcement announcement = Announcement.builder()

                .memberIdx(req.getMemberIdx())
                .title(req.getTitle())
                .content(req.getContent())
                .regDt(LocalDateTime.now())
                .build();

        announcementRepository.save(announcement);

        return true;
    }


    public Announcement read(Long announcementId) {
        Optional<Announcement> announcementOptional = announcementRepository.findById(announcementId);
        return announcementOptional.orElse(null);
    }

    public List<Announcement> list() {
        return announcementRepository.findAll();
    }


    public boolean update(Long announcementId, AnnouncementUpdateReq req) {
        Optional<Announcement> announcementOptional = announcementRepository.findById(announcementId);

        if (announcementOptional.isPresent()) {
            Announcement announcement = announcementOptional.get();
            announcement.setTitle(req.getTitle());
            announcement.setContent(req.getContent());
            announcement.setUdtDt(LocalDateTime.now());
            announcementRepository.save(announcement);
            return true;
        } else {
            return false; // 해당 ID의 공지사항이 존재하지 않음
        }
    }

    public boolean delete(Long announcementId) {
        Optional<Announcement> announcementOptional = announcementRepository.findById(announcementId);

        if (announcementOptional.isPresent()) {
            announcementRepository.deleteById(announcementId);
            return true;
        } else {
            return false; // 해당 ID의 공지사항이 존재하지 않음
        }
    }


}
