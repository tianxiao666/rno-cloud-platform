package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ke_weixu
 */
@Data
@Entity
@Table(name = "RNO_SYS_APP")
public class App {

  @Id
  @GeneratedValue(generator = "AppSeq")
  @SequenceGenerator(name = "AppSeq", sequenceName = "SEQ_RNO_SYS_APP", allocationSize = 1)

  private Long id;
  private String code;
  private String name;
  private String version;
  private String logo;
  private String description;

}
