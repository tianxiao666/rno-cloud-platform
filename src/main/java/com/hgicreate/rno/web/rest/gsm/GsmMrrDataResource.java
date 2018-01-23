package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileAttrRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import com.hgicreate.rno.service.gsm.GsmMrrDataService;
import com.hgicreate.rno.service.gsm.dto.GsmMrrDataQueryDTO;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
