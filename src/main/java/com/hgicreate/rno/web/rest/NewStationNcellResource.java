package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.NewStationNcellService;
import com.hgicreate.rno.service.dto.NewStationNcellDescQueryDTO;
import com.hgicreate.rno.service.dto.NewStationNcellImportQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNewStationNcellDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNewStationNcellUploadVM;
import com.hgicreate.rno.web.rest.vm.NewStationNcellImportQueryVM;
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
@RequestMapping("/api/gsm-new-station-ncell")
public class NewStationNcellResource {
    private final NewStationNcellService newStationNcellService;

    public NewStationNcellResource(NewStationNcellService newStationNcellService) {
        this.newStationNcellService = newStationNcellService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> gsmNewStationNcellUpload(GsmNewStationNcellUploadVM vm) {
        return newStationNcellService.gsmNewStationNcellUpload(vm);
    }

    @PostMapping("/desc-query")
    public List<NewStationNcellDescQueryDTO> descQuery(GsmNewStationNcellDescQueryVM vm) {
        return newStationNcellService.descQuery(vm);
    }

    @PostMapping("/import-query")
    public List<NewStationNcellImportQueryDTO> importQuery(NewStationNcellImportQueryVM vm) {
        return newStationNcellService.importQuery(vm);
    }
}
