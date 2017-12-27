package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.service.gsm.GsmNcsService;
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
public class GsmNcsResource {
    private final GsmNcsService gsmNcsService;

    public GsmNcsResource(GsmNcsService gsmNcsService) {
        this.gsmNcsService = gsmNcsService;
    }

    @PostMapping("/cell-ncs-query")
    public List<GsmNcsAnalysisDTO> cellNcsQuery(CellNcsQueryVM vm) {
        return gsmNcsService.cellNcsQuery(vm);
    }

    @PostMapping("/ncs-desc-query")
    public GsmEriNcsDesc ncsDescQuery(Long ncsId) {
        return gsmNcsService.queryGsmEriNcsDesc(ncsId);
    }
}
