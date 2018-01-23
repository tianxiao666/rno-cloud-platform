package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmDtDesc;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import com.hgicreate.rno.service.gsm.GsmDtDataService;
import com.hgicreate.rno.service.gsm.dto.GsmDtDescListDTO;
import com.hgicreate.rno.web.rest.gsm.vm.FindGsmDtDescVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmDtUploadVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
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
@RequestMapping("/api/gsm-dt")
public class GsmDtDataResource {
    private final GsmDtDataService gsmDtDataService;

    public GsmDtDataResource(GsmDtDataService gsmDtDataService) {
        this.gsmDtDataService = gsmDtDataService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(GsmDtUploadVM vm) {
        return gsmDtDataService.gsmDtUpload(vm);
    }

    @PostMapping("/find-dt-desc-list")
    public List<GsmDtDescListDTO> findDtDescList(FindGsmDtDescVM vm) {
        return gsmDtDataService.findGsmDtDescList(vm);
    }

    @PostMapping("/gsm-import-query")
    public List<GsmImportQueryDTO> gsmImportQuery(GsmImportQueryVM vm){
        return gsmDtDataService.gsmImportQuery(vm);
    }

    @PostMapping("/query-report")
    public List<DataJobReportDTO> queryReport(String id){
        return gsmDtDataService.queryReport(id);
    }
}
