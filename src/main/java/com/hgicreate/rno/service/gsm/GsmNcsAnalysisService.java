package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.mapper.gsm.GsmNcsAnalysisMapper;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.service.gsm.dto.GsmNcsAnalysisDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmEriNcsDescMapper;
import com.hgicreate.rno.web.rest.gsm.vm.CellNcsQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmNcsAnalysisService {
    private final GsmNcsAnalysisMapper gsmNcsAnalysisMapper;
    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    public GsmNcsAnalysisService(GsmNcsAnalysisMapper gsmNcsAnalysisMapper, GsmEriNcsDescRepository gsmEriNcsDescRepository) {
        this.gsmNcsAnalysisMapper = gsmNcsAnalysisMapper;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
    }

    /**
     * NCS邻区分析查询
     */
    public List<GsmNcsAnalysisDTO> cellNcsQuery(CellNcsQueryVM vm){
        List<String> ncells = gsmNcsAnalysisMapper.queryGsmNcell(vm.getCell());
        if (Objects.equals(vm.getFactory(), "ERI")){
        List<GsmNcsAnalysisDTO> result = gsmNcsAnalysisMapper.queryGsmEriNcs(vm.getCityId(), vm.getCell());
            for (GsmNcsAnalysisDTO dto:result) {
                dto.setManufacturers("ERI");
                if (dto.getDefined() != 1L && ncells.contains(dto.getNcell())) {
                    dto.setDefined(1L);
                }
            }
            return result;
        }
        if (Objects.equals(vm.getFactory(), "HW")) {
            List<GsmNcsAnalysisDTO> result = gsmNcsAnalysisMapper.queryGsmHwNcs(vm.getCityId(), vm.getCell());
            for (GsmNcsAnalysisDTO dto:result) {
                dto.setManufacturers("HW");
                if (ncells.contains(dto.getNcell())) {
                    dto.setDefined(1L);
                }else {
                    dto.setDefined(0L);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * NCS邻区分析测量信息查询
     */
    public GsmNcsDescQueryDTO queryGsmNcsDesc(Long ncsDescId) {
        GsmEriNcsDesc gsmEriNcsDesc = gsmEriNcsDescRepository.findOne(ncsDescId);
        return GsmEriNcsDescMapper.INSTANCE.gsmNcsDescQueryToDTO(gsmEriNcsDesc);
    }
}
