package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class GsmUploadVM {
    private MultipartFile file;
    private Long cityId;
    private Date fileDate;
    private String importFactory;
    private String moduleName;
}
