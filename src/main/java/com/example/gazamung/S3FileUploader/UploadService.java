package com.example.gazamung.S3FileUploader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.FileType;
import com.example.gazamung.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class UploadService {
    private final AmazonS3Client amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    private final UploadRepository uploadRepository;



    /**
     * @param multipartFile     업로드된 파일 multipartFile 객체
     * @param memberIdx            사용자 m
     * @param attachmentType    업로드 타입("REVIEW,GENERAL,CLUB")
     * @return
     * @throws IOException
     * @title 파일 업로드
     */

    //@TODO  회원에 대한 예외처리가 필요한지 확인해보기
    @Transactional
    public List<Map<String, Object>> upload(List<MultipartFile> multipartFile, long memberIdx, AttachmentType attachmentType, long mappedId) {

        try {
            List<UploadImage> saveImageDataList = new ArrayList<>();

            if (multipartFile.size() <= 0) {
                new IllegalArgumentException("업로드될 파일이 없습니다..");
            }

            //MultipartFile로 받은 객체를 File 객체로 변환
            for (MultipartFile file : multipartFile) {
                //업로드 시도한 파일 제한 용량 검증
                validateFileSize(file);
                //MultipartFile -> File 객체로 convert
                File convertFile = convert(file).orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
                //s3에 적재될 유니크한 파일 이름
                String saveFileName = generateUniqueFileName(file.getName());
                //실제 파일 이름.
                String orginFileName = file.getOriginalFilename();
                //업로드될 버킷 PATH
                String bucketPath = attachmentType.getType() + "/" + memberIdx + "/" + mappedId + "/" + saveFileName;//적재할 경로 세팅
                //업로드 된 이미지 URL
                String url = putS3(convertFile, bucketPath); //s3에 적재
                // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
                removeNewFile(convertFile);
                //S3 버킷에 적재된 이미지 파일 정보를 게시글정보와 함께 맵핑해서 디비에 저장함.
                saveImageDataList.add(UploadImage.builder()
                        .fileName(orginFileName)
                        .uniqueFileName(saveFileName)
                        .imageUrl(url)
                        .fileSize(file.getSize())
                        .attachmentType(attachmentType.getType())
                        .uploaderIdx(memberIdx)
                        .mappedId(mappedId)
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build());
            }

            return saveDB(saveImageDataList, attachmentType, mappedId);
        } catch (CustomException e) {
            new CustomException(CustomExceptionCode.SERVER_ERROR);
        }
        return null;
    }


    /**
     * @title AttachmentType에 맞는 이미지 데이터를 반환합니다.
     * @param attachmentType {"reivew","general","club"}
     * @param mappedId {"reviewId","feedId","clubId"}
     * @return [data]
     */
    public List<UploadImage> getImageByAttachmentType(AttachmentType attachmentType, long mappedId) {
        List<UploadImage> byAttachmentTypeAndMappedId = uploadRepository.findByAttachmentTypeAndMappedId(attachmentType.getType(), mappedId);
        return byAttachmentTypeAndMappedId;
    }

    /**
     * @title Image pk를 통한 이미지 데이터 반환
     * @param id : db pk
     * @return
     */
    public Optional<UploadImage> getImageById(Long id) {
        return uploadRepository.findById(id);
    }


/**
 * @title S3에 적재된 파일과 맵핑 정보를 데이터베이스에 저장합니다.
 * @param saveImageData 맵핑된 이미지 정보.
 * @param attachmentType
 * @param mappedId
 * @return List<Map<String, Object>>
 */
private List<Map<String, Object>> saveDB(List<UploadImage> saveImageData, AttachmentType attachmentType, long mappedId) {
    List<UploadImage> existingImages = uploadRepository.findByAttachmentTypeAndMappedId(attachmentType.getType(), mappedId);

    // 이미 해당 리뷰 또는 피드에 업로드된 이미지가 있는 경우, 모두 삭제
    if (!existingImages.isEmpty()) {
        uploadRepository.deleteAll(existingImages);
    }

    // 새로운 이미지들을 저장
    List<UploadImage> savedImages = uploadRepository.saveAll(saveImageData);

    // 저장된 이미지들의 정보를 결과 리스트에 추가
    List<Map<String, Object>> results = new ArrayList<>();

    for (UploadImage savedImage : savedImages) {
        Map<String, Object> image = new HashMap<>();
        image.put("fileName", savedImage.getFileName());
        image.put("imageUrl", savedImage.getImageUrl());
        results.add(image);
    }

    return results;
}

    /**
     * @title S3 업로드 모듈
     * @param uploadFile
     * @param fileName
     * @return
     */
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * DB에 맵핑되어있는 업로드 정보 삭제
     */
    public void removeDatabaseByReviewIdx(long mappedIdx) {
        try {
            uploadRepository.deleteByMappedId(mappedIdx);
        } catch(CustomException e) {
            System.out.println("Exception removeDatabaseByReviewIdx : " + e);
            log.error("Exception removeDatabaseByReviewIdx : " + e);
            throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        }
    }

    /**
     * @title S3 다중 파일 객체 삭제.
     * @param filePath
     * @Author 이시영
     */
    public void removeS3Files(String[] filePath) {
        try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucket)
                    .withKeys(filePath);

            amazonS3Client.deleteObjects(dor);
            System.out.println("S3 Object 삭제 성공 : " + filePath);
        } catch (AmazonServiceException e) {
            System.out.println("S3 Object 삭제 실패 : " + e);
            log.debug("S3 Object 삭제 실패 : ", e);
            System.err.println(e.getErrorMessage());
        }
    }

    /**
     * @title 버킷에 저장되어있는 단건 파일을 삭제합니다.
     * @param filePath : 파일 이름을 포함하는 버킷 path
     */
    public void removeS3File(String filePath) {
        try {
            //버킷이 존재하는지 검증
            boolean isBucketExist = amazonS3Client.doesBucketExist(bucket);

            //버킷이 존재하는경우 오브젝트 삭제 진행
            if (isBucketExist) {
                amazonS3Client.deleteObject(bucket, filePath);
                System.out.println("S3 Object 삭제 성공 : " + filePath);
            } else {
                throw new CustomException(CustomExceptionCode.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("S3 Object 삭제 실패 : " + e);
            log.debug("S3 Object 삭제 실패 : ", e);
        }
    }

    /**
     * S3 적재전 로컬에 임시 저장되어있는 파일을 삭제합니다.
     * @param targetFile
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
    /**
     * @title 파일이름이 겹치지 않기 위한 유니크한 파일 이름을 만들어주는 함수.
     * @param originalFileName 업로드하는 파일의 원본 이름
     * @return uuid
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        // UUID를 사용하여 랜덤값을 생성하고, 확장자와 합쳐서 고유한 파일 이름을 생성
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID + extension;
    }

    /**
     * @title multipart 파일 객체를 일반 File 객체로 변환
     * @param file
     * @author 이시영
     * @return
     */
    private Optional<File> convert(MultipartFile file) {
        try {
            File convertFile = new File(file.getOriginalFilename());

            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                return Optional.of(convertFile);
            }
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("e : " + e);
        }
        return Optional.empty();
    }

    /**
     * @title 파일의 최대 용량을 초과한 경우 예외 처리
     * @param file
     */
    private void validateFileSize(MultipartFile file) {
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        FileType fileType = FileType.fromContentType(contentType);

        if (fileType != null) {
            switch (fileType) {
                case PNG:
                case JPEG:
                    if (fileSize > fileType.getMaxSize()) {
                        System.out.println(fileType.getContentType() + " : 업로드 제한된 용량 이상입니다.");
                        // ZIP 파일의 최대 용량을 초과한 경우 예외 처리
                        // throw new YourException("ZIP 파일 용량 초과"); // 예외 처리 방식은 상황에 맞게 정의
                    }
                    break;
                default:
                    break;
            }
        }
    }






}