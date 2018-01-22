package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

/**
 * @author tao.xj
 */
@Data
public class GsmStructAnalysisQueryVM {
    private Long cityId;
    private String taskName;
    private String taskStatus;
    private String measureDate;
    private String begDate;
    private String endDate;
    private Boolean isMine;
    private String createdUser;
}
