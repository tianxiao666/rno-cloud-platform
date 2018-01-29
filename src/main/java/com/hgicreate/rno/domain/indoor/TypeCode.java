package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "INDOOR_TYPE_CODE")
public class TypeCode {

  @Id
  private String codeType;
  private String codeName;
  private String maxLength;
  private String codeNote;
  private String createTime;
}
