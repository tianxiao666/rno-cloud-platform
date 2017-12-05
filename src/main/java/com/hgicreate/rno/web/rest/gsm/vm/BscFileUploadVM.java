package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BscFileUploadVM {
    private String moduleName;
    private String areaId;
    private MultipartFile file;
    private String sourceType;
    private String importModel;
}
