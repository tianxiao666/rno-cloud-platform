package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmDtDesc;
import com.hgicreate.rno.service.gsm.GsmDtService;
import com.hgicreate.rno.web.rest.gsm.vm.FindGsmDtDescVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmDtUploadVM;
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
public class GsmDtResource {
    private final GsmDtService gsmDtService;

    public GsmDtResource(GsmDtService gsmDtService) {
        this.gsmDtService = gsmDtService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(GsmDtUploadVM vm) {
        return gsmDtService.gsmDtUpload(vm);
    }

    @PostMapping("/find-dt-desc-list")
    public List<GsmDtDesc> findDtDescList(FindGsmDtDescVM vm) {
        return gsmDtService.findGsmDtDescList(vm);
    }
}
