package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GsmNewStationNcellUploadVM {
    private Long areaId;
    private MultipartFile file;
    private String fileCode;
}
