package com.hgicreate.rno.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/lte-ncell-relation")
public class LteNcellRelationResource {

    @PostMapping("/ncell-query")
    public void ncellQuery(String cellName,String ncellName,String cellSite,String ncellSite){
        log.debug("查询条件：主小区名称="+cellName+",邻小区名称="+ncellName+"，主小区site="+cellSite+"，邻小区site="+ncellSite);
    }
}
