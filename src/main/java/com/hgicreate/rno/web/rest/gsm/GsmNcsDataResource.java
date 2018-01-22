package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmNcsDescService;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsDescQueryVM;
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
@RequestMapping("/api/gsm-ncs-data")
public class GsmNcsDataResource {
    private final GsmNcsDescService gsmNcsDescService;

    public GsmNcsDataResource(GsmNcsDescService gsmNcsDescService) {
        this.gsmNcsDescService = gsmNcsDescService;
    }

    @PostMapping("/gsm-ncs-data-query")
    public List<GsmNcsDescQueryDTO> gsmNcsDataQuery(GsmNcsDescQueryVM vm){
        return gsmNcsDescService.ncsDescQuery(vm);
    }
}
