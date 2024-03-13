package com.example.gazamung.S3FileUploader;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class UploadImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILEUPLOAD_SEQ")
    @SequenceGenerator(name = "FILEUPLOAD_SEQ", sequenceName = "fileupload_sequence", allocationSize = 1)
    private Long idx;

    private Long uploaderIdx;   //업로더 회원idx
    private String fileName;    //파일 이름
    private String uniqueFileName; //적재된 파일명
    private String imageUrl; //파일 이미지 URL
    private long fileSize; //파일 용량
    private String attachmentType; //리뷰,피드
    private long mappedId;//연결된 리뷰,피드 게시글
    private LocalDateTime created;
    private LocalDateTime updated;


}
