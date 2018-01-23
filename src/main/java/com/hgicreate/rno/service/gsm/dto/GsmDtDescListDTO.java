package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class GsmDtDescListDTO {
    private String name;
    private String netMode;
    private String type;
    private Date testDate;
    private String vendor;
    private String device;
    private String version;
}
