package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.repository.CellDataRepository;
import com.hgicreate.rno.service.CellDataService;
import com.hgicreate.rno.service.dto.CellDataDTO;
import com.hgicreate.rno.web.rest.vm.CellDataVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-data")
public class LteCellDataResource {

    private final CellDataRepository cellDataRepository;

    private final CellDataService cellDataService;

    public LteCellDataResource(CellDataRepository cellDataRepository, CellDataService cellDataService) {
        this.cellDataRepository = cellDataRepository;
        this.cellDataService = cellDataService;
    }

    @PostMapping("/cell-query")
    public List<CellDataDTO> cellQuery(CellDataVM cellDataVM){
        log.debug("查询条件：省="
                +cellDataVM.getProvinceId()+",市="
                +cellDataVM.getCityId()+"，LTE CELL ID="
                +cellDataVM.getCellId()+"，LTE CELL名称="
                +cellDataVM.getCellName()+"，小区PCI="
                +cellDataVM.getPci());
        return cellDataService.queryCellByCondition(cellDataVM);
    }

    @GetMapping("/findCellDetailById")
    public List<Cell> findCellDetailById(@RequestParam String cellId){
       String enodebId= cellDataRepository.findOne(cellId).getEnodebId();
        return cellDataRepository.findByEnodebId(enodebId);
    }

    @GetMapping("/findCellDetailForEdit")
    public Cell findCellDetailForEdit(@RequestParam String cellId){
        return cellDataRepository.findOne(cellId);
    }

    @GetMapping("/deleteByCellId")
    public void deleteByCellId(@RequestParam String cellId){
        log.debug("待删除小区id为={}", cellId);
        cellDataRepository.delete(cellId);
    }

    @PostMapping("/updateLteCellDetail")
    public boolean updateLteCellDetail(Cell cell){
        try {
            log.debug("要更新的小区={}", cell.getLatitude());
            cellDataRepository.save(cell);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }


}
