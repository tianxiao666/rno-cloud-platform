package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yang.ch1
 */
@Data
public class GsmTrafficFileUploadVM {

    private String moduleName;
    private String areaId;
    private String cityId;
    private MultipartFile file;
    private String indicatorType;
    private String handleReplicateMethod;
}
