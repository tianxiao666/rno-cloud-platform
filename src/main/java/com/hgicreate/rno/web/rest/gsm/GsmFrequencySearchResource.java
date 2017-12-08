package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.web.rest.gsm.vm.GsmFrequencyUpdateVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/gsm-frequency-research")
public class GsmFrequencySearchResource {

    private final GsmCellDataRepository gsmCellDataRepository;

    public GsmFrequencySearchResource(GsmCellDataRepository gsmCellDataRepository) {
        this.gsmCellDataRepository = gsmCellDataRepository;
    }

    @PostMapping("/update-cell-freq-by-cellId")
    public void updateCellFreqByCellId(GsmFrequencyUpdateVM vm){
        log.debug("更新频点参数bcch={},tch={},cellId={}",
                vm.getBcch(),vm.getTch(),vm.getCellId());
        GsmCell gsmCell = gsmCellDataRepository.findOne(vm.getCellId());
        gsmCell.setBcch(Integer.parseInt(vm.getBcch()));
        gsmCell.setTch(vm.getTch());
        gsmCellDataRepository.save(gsmCell);
    }
}
