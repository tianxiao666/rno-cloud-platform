package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_SYS_USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;

    private Long defaultArea;

    private String createdUser;
    private Date createdDate;

    private String lastModifiedUser;
    private Date lastModifiedDate;
}
