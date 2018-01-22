package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import com.hgicreate.rno.service.gsm.GsmFasDataService;
import com.hgicreate.rno.web.rest.gsm.vm.GsmFasDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-fas-data")
public class GsmFasDataResource {
    private final GsmFasDataService gsmFasDataService;

    public GsmFasDataResource(GsmFasDataService gsmFasDataService) {
        this.gsmFasDataService = gsmFasDataService;
    }

    @PostMapping("/data-query")
    public List<GsmFasDesc> gsmFasDataQuery(GsmFasDataQueryVM vm){
        return gsmFasDataService.gsmFasDescQuery(vm);
    }
}
