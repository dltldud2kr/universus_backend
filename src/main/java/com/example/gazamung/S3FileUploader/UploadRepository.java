package com.example.gazamung.S3FileUploader;

import com.example.gazamung._enum.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadRepository extends JpaRepository<UploadImage,Long> {

    List<UploadImage> findByAttachmentTypeAndMappedId(String attachmentType, long mappedId);

    void deleteByMappedId(long mappedIdx);

    Optional<UploadImage> findByAttachmentTypeAndMappedId(String attachmentType,Long mappedId );

}
