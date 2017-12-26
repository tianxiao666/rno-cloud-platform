package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmEriNcsService;
import com.hgicreate.rno.service.gsm.dto.GsmNcsAnalysisDTO;
import com.hgicreate.rno.web.rest.gsm.vm.CellNcsQueryVM;
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
@RequestMapping("/api/gsm-ncs-analysis")
public class GsmEriNcsResource {
    private final GsmEriNcsService gsmEriNcsService;

    public GsmEriNcsResource(GsmEriNcsService gsmEriNcsService) {
        this.gsmEriNcsService = gsmEriNcsService;
    }

    @PostMapping("/cell-ncs-query")
    public List<GsmNcsAnalysisDTO> cellNcsQuery(CellNcsQueryVM vm) {
        return gsmEriNcsService.cellNcsQuery(vm);
    }
}
