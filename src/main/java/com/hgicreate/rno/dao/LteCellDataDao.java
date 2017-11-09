package com.hgicreate.rno.dao;


import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class LteCellDataDao {

    private final EntityManager entityManager;

    public LteCellDataDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public List<LteCellDataDTO> queryLteCellByCondition(LteCellDataVM lteCellDataVM){
        String sql = "select a.cell_id as cellId, b.name as areaName, a.cell_name as cellName,a.pci as pci\n" +
                ", a.band_width as bandWidth, a.earfcn as earfcn, a.azimuth from RNO_LTE_CELL a\n" +
                "join RNO_SYS_AREA b on (a.AREA_ID = b.ID) where ROWNUM <1001";
        sql += " and a.area_id =" + lteCellDataVM.getCityId();
        if(!lteCellDataVM.getCellId().trim().equals("")){
            sql += " and a.cell_id = '" + lteCellDataVM.getCellId().trim()+"'";
        }
        if(!lteCellDataVM.getCellName().trim().equals("")){
            sql += " and a.cell_name like %"+ lteCellDataVM.getCellName().trim()+"%";
        }
        if(!lteCellDataVM.getPci().trim().equals("")){
            sql += " and a.pci =" + lteCellDataVM.getPci().trim();
        }
        Query query =entityManager.createNativeQuery(sql);

        return (List<LteCellDataDTO>) query.getResultList();
    }
}
