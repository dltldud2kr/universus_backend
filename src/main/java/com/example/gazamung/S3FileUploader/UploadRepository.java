package com.example.gazamung.S3FileUploader;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadRepository extends JpaRepository<UploadImage,Long> {

    List<UploadImage> findByAttachmentTypeAndMappedId(String attachmentType, long mappedId);

    void deleteByMappedId(long mappedIdx);

}
