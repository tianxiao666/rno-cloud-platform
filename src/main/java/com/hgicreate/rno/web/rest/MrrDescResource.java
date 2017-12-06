package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.MrrDesc;
import com.hgicreate.rno.service.MrrDescService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class MrrDescResource {
    private final MrrDescService mrrDescService;

    public MrrDescResource(MrrDescService mrrDescService) {
        this.mrrDescService = mrrDescService;
    }

    @PostMapping("/gsm-mrr-data-query")
    public List<MrrDesc> gsmMrrDateQuery(String cityName, String factory, String bsc, Date beginTestDate, Date endTestDate) {
        return mrrDescService.mrrDataQuery(cityName, factory, bsc, beginTestDate, endTestDate);
    }
}
