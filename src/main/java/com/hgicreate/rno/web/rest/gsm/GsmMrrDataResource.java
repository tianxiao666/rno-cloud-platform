package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import com.hgicreate.rno.service.gsm.GsmMrrDataService;
import com.hgicreate.rno.service.gsm.dto.GsmMrrDataQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-mrr-data")
public class GsmMrrDataResource {
    private final GsmMrrDataService gsmMrrDataService;

    public GsmMrrDataResource(GsmMrrDataService gsmMrrDataService) {
        this.gsmMrrDataService = gsmMrrDataService;
    }

    @PostMapping("/gsm-mrr-data-query")
    public List<GsmMrrDataQueryDTO> gsmMrrDataQuery(GsmMrrDescQueryVM vm) {
        return gsmMrrDataService.mrrDataQuery(vm);
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(GsmUploadVM vm) {
        return gsmMrrDataService.uploadFile(vm);
    }

    @PostMapping("/gsm-import-query")
    public List<GsmImportQueryDTO> gsmImportQuery(GsmImportQueryVM vm){
        return gsmMrrDataService.gsmImportQuery(vm);
    }

    @PostMapping("/query-mrr-detail")
    @ResponseBody
    public List<Map<String, Object>> queryMrrDetail(Long mrrDescId){
        return gsmMrrDataService.queryEriMrrDetailByPage(mrrDescId);
    }

    @PostMapping("/query-report")
    public List<DataJobReportDTO> queryReport(String id){
        return gsmMrrDataService.queryReport(id);
    }
}
