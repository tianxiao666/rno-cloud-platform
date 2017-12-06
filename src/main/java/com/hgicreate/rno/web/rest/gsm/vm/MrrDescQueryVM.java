package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class MrrDescQueryVM {
    private  Long areaId;
    private String factory;
    private String bsc;
    private Date beginTestDate;
    private Date endTestDate;
}
