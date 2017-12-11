package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import com.hgicreate.rno.service.gsm.GsmFasService;
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
public class GsmFasResource {
    private final GsmFasService gsmFasService;

    public GsmFasResource(GsmFasService gsmFasService) {
        this.gsmFasService = gsmFasService;
    }

    @PostMapping("/gsm-fas-data-query")
    public List<GsmFasDesc> gsmFasDataQuery(GsmFasDataQueryVM vm){
        return gsmFasService.gsmFasDescQuery(vm);
    }
}
