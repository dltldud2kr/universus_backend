package com.example.gazamung.S3;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILEUPLOAD_SEQ")
    @SequenceGenerator(name = "FILEUPLOAD_SEQ", sequenceName = "fileupload_sequence", allocationSize = 1)
    private Long id;

    private String fileName;
    private String s3Url;

}
