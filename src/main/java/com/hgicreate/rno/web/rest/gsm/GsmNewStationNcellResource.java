package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmNewStationNcellService;
import com.hgicreate.rno.service.gsm.dto.GsmNewStationNcellDescQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNewStationNcellDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNewStationNcellUploadVM;
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
public class GsmNewStationNcellResource {
    private final GsmNewStationNcellService gsmNewStationNcellService;

    public GsmNewStationNcellResource(GsmNewStationNcellService gsmNewStationNcellService) {
        this.gsmNewStationNcellService = gsmNewStationNcellService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> gsmNewStationNcellUpload(GsmNewStationNcellUploadVM vm) {
        return gsmNewStationNcellService.gsmNewStationNcellUpload(vm);
    }

    @PostMapping("/desc-query")
    public List<GsmNewStationNcellDescQueryDTO> descQuery(GsmNewStationNcellDescQueryVM vm) {
        return gsmNewStationNcellService.descQuery(vm);
    }
}
