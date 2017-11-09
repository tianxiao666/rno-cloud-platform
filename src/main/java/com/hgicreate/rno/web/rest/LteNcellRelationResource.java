package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.NcellRelation;
import com.hgicreate.rno.repository.NcellRepository;
import com.hgicreate.rno.service.NcellRelationService;
import com.hgicreate.rno.service.dto.NcellRelationDTO;
import com.hgicreate.rno.web.rest.vm.NcellRelationQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lte-ncell-relation")
public class LteNcellRelationResource {

    private final NcellRelationService ncellRelationService;
    private final NcellRepository ncellRepository;

    public LteNcellRelationResource(NcellRepository ncellRepository,NcellRelationService ncellRelationService ) {
        this.ncellRepository = ncellRepository;
        this.ncellRelationService = ncellRelationService;
    }

    @PostMapping("/ncell-query")
    public List<NcellRelationDTO> ncellQuery(NcellRelationQueryVM vm) {
        log.debug("查询LTE邻区关系");
        return ncellRelationService.queryNcellRelationDTOs(vm);
    }

    @GetMapping("/deleteByCellIdAndNcellId")
    public void deleteByCellId(@RequestParam long id){
        log.debug("待删除邻区id为={}", id);
        ncellRepository.delete(id);
    }
}
