package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_ORIGIN_FILE")
public class OriginFile {

    @Id
    @GeneratedValue(generator = "OriginFileSeq")
    @SequenceGenerator(name = "OriginFileSeq", sequenceName = "SEQ_ORIGIN_FILE", allocationSize = 5)
    private Long id;

    private String filename;
    private String fileType;
    private Integer fileSize;
    private String fullPath;
    private String dataType;
    private Integer dataAttr;
    private String sourceType;
    private String createdUser;
    private Date createdDate;

}
