package com.hgicreate.rno.web.rest.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LteMrDataFileUploadVM {
    private String moduleName;
    private String areaId;
    private MultipartFile file;
    private String recordDate;
    private String vendor;
}
