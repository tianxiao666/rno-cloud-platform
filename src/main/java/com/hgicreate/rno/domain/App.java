package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "RNO_SYS_APP")
public class App {

  @Id
  private Long id;
  private String code;
  private String name;
  private String version;
  private String logo;
  private String description;

}
