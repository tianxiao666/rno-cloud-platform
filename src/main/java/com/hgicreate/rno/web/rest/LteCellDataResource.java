package com.hgicreate.rno.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-data")
public class LteCellDataResource {
    @PostMapping("/cell-query")
    public void cellQuery(String provinceId,String cityId,String enodebName,String cellName,String pci){
        log.debug("查询条件：省="+provinceId+",市="+cityId+"，eNodeB名称="+enodebName+"，LTE CELL名称="+cellName+"，小区PCI="+pci);
    }
}
