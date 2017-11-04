package com.hgicreate.rno.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Tao.xj
 * @Description:
 * @Date:Created in 11:37 2017/11/4
 */
@Slf4j
@RestController
@RequestMapping("/api/lte-ncell-relation")
public class LteNcellRelationResource {

    private static final Logger logger = LoggerFactory.getLogger(LteNcellRelationResource.class);

    @PostMapping("/ncellQuery")
    public void ncellQuery(String cellName,String ncellName,String cellSite,String ncellSite){
        logger.debug("查询条件：主小区名称="+cellName+",邻小区名称="+ncellName+"，主小区site="+cellSite+"，邻小区site="+ncellSite);
    }
}
