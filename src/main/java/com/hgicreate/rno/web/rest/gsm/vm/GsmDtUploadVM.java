package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GsmDtUploadVM {
    private MultipartFile file;
    private Long areaId;
    private String taskName;
    private String fileCode;
}
