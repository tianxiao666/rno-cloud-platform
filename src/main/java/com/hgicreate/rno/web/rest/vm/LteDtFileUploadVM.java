package com.hgicreate.rno.web.rest.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LteDtFileUploadVM {
    private String moduleName;
    private String areaId;
    private MultipartFile file;
    private String business_type;
    private String area_type;
    private String sourceType;
}
