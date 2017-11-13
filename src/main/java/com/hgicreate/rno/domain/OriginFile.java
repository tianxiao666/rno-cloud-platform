package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "RNO_ORIGIN_FILE")
public class OriginFile {

    @Id
    private Long id;

    private String filename;
    private String fileType;
    private Integer fileSize;
    private String fullPath;
    private String dataType;
    private Integer dataAttr;
    private String sourceType;
    private String createdUser;
    private LocalDateTime createdDate;
}
