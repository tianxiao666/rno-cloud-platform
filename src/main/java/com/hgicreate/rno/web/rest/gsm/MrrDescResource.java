package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.MrrDesc;
import com.hgicreate.rno.service.gsm.MrrDescService;
import com.hgicreate.rno.web.rest.gsm.vm.MrrDescQueryVM;
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
    public List<MrrDesc> gsmMrrDateQuery(MrrDescQueryVM vm) {
        return mrrDescService.mrrDataQuery(vm);
    }
}
