package com.hgicreate.rno.service.gsm.dto;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class GsmNcsDescQueryDTO {
    private String bsc;
    private Date meaTime;
    private Long recordCount;
    private Long numfreq;
    private Long rectime;
    private String rid;
    private Long termReason;
    private Long ecnoabss;
    private Long relss;
    private Long relss2;
    private Long relss3;
    private Long relss4;
    private Long relss5;
    private Long ncelltype;
    private Long nucelltype;
    private Long tfddmrr;
    private Long numumfi;
}
