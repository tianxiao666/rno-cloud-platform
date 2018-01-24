package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import com.hgicreate.rno.service.gsm.GsmFasDataService;
import com.hgicreate.rno.service.gsm.dto.GsmFasDataQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmFasDataQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public List<GsmFasDataQueryDTO> gsmFasDataQuery(GsmFasDataQueryVM vm){
        return gsmFasDataService.gsmFasDescQuery(vm);
    }

    @PostMapping("/gsm-import-query")
    public List<GsmImportQueryDTO> gsmImportQuery(GsmImportQueryVM vm){
        return gsmFasDataService.gsmImportQuery(vm);
    }

    @PostMapping("/query-report")
    public List<DataJobReportDTO> queryReport(String id){
        return gsmFasDataService.queryReport(id);
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(GsmUploadVM vm) {
        return gsmFasDataService.uploadFile(vm);
    }
}
