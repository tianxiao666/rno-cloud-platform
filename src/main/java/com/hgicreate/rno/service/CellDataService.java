package com.hgicreate.rno.service;

import com.hgicreate.rno.service.dto.CellDataDTO;
import com.hgicreate.rno.web.rest.vm.CellDataVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;


@Slf4j
@Service
@Transactional
public class CellDataService {


    private final EntityManager entityManager;

    public CellDataService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public List<CellDataDTO> queryLteCell(CellDataVM cellDataVM){
        String sql = "select a.cell_id as cellId, b.name as areaName, a.cell_name as cellName,a.pci as pci\n" +
                ", a.band_width as bandWidth, a.earfcn as earfcn, a.azimuth from RNO_LTE_CELL a\n" +
                "join RNO_SYS_AREA b on (a.AREA_ID = b.ID) where ROWNUM <1001";
        sql += " and a.area_id =" + cellDataVM.getCityId();
        if(!cellDataVM.getCellId().trim().equals("")){
            sql += " and a.cell_id = " + cellDataVM.getCellId().trim();
        }
        if(!cellDataVM.getCellName().trim().equals("")){
            sql += " and a.cell_name like % "+cellDataVM.getCellName().trim()+" %";
        }
        if(!cellDataVM.getPci().trim().equals("")){
            sql += " and a.pci =" + cellDataVM.getPci().trim();
        }
        Query query =entityManager.createNativeQuery(sql);

        return (List<CellDataDTO>) query.getResultList();

    }
}
