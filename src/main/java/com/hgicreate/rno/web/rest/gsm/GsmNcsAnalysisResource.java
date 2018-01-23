package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.service.gsm.GsmNcsAnalysisService;
import com.hgicreate.rno.service.gsm.dto.GsmNcsAnalysisDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
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
public class GsmNcsAnalysisResource {
    private final GsmNcsAnalysisService gsmNcsAnalysisService;

    public GsmNcsAnalysisResource(GsmNcsAnalysisService gsmNcsAnalysisService) {
        this.gsmNcsAnalysisService = gsmNcsAnalysisService;
    }

    @PostMapping("/cell-ncs-query")
    public List<GsmNcsAnalysisDTO> cellNcsQuery(CellNcsQueryVM vm) {
        return gsmNcsAnalysisService.cellNcsQuery(vm);
    }

    @PostMapping("/ncs-desc-query")
    public GsmNcsDescQueryDTO ncsDescQuery(Long ncsId) {
        return gsmNcsAnalysisService.queryGsmNcsDesc(ncsId);
    }
}
