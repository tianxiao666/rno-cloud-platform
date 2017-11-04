package com.hgicreate.rno.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/lte-grid-data")
public class LteGridDataResource {
    @PostMapping("/import-query")
    public void importQuery(String provinceId,String cityId,String begUploadDate,String endUploadDate,String fileStatus){
        log.debug("查询条件：省="+provinceId+",市="+cityId+"，上传时间=从"+begUploadDate+"到"+endUploadDate+"，状态="+fileStatus);
    }

    @PostMapping("/data-query")
    public void dataQuery(String provinceId2,String cityId2){
        log.debug("查询条件：省="+provinceId2+",市="+cityId2);
    }
}
