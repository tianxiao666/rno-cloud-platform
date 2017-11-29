package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "RNO_ORIGIN_FILE_ATTR")
public class OriginFileAttr implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OriginFileAttrSeq")
    @SequenceGenerator(name = "OriginFileAttrSeq", sequenceName = "SEQ_ORIGIN_FILE_ATTR", allocationSize = 1)
    private Long id;

    private String name;
    private String value;

    @ManyToOne
    @JoinColumn(name = "origin_file_id")
    private OriginFile originFile;

}
