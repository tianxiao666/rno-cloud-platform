package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "RNO_ORIGIN_FILE_ATTR")
public class OriginFileAttr {
    @Id
    @GeneratedValue(generator = "OriginFileAttrSeq")
    @SequenceGenerator(name = "OriginFileAttrSeq", sequenceName = "SEQ_ORIGIN_FILE_ATTR", allocationSize = 1)
    private Long id;

    private Integer originFileId;
    private String name;
    private String value;

}
