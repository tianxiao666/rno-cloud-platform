package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.NcellRelation;
import com.hgicreate.rno.service.dto.NcellRelationDTO;
import com.hgicreate.rno.service.mapper.NcellRelationMapper;
import com.hgicreate.rno.web.rest.vm.NcellRelationQueryVM;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityResult;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NcellRelationService {

    private final EntityManager entityManager;

    public NcellRelationService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public List<NcellRelationDTO> queryNcellRelationDTOs(NcellRelationQueryVM vm) {
        String sql = new String();
        sql = "select c.id,a.cell_id as cell_id, a.cell_name as cell_name,a.enodeb_id as cell_Enodeb_Id,a.pci as cell_Pci,b.cell_id as ncell_Id,b.cell_name as ncell_Name,b.enodeb_id as ncell_Enodeb_Id,b.pci as ncell_Pci from rno_lte_cell a,rno_lte_cell b,rno_lte_ncell_relation c where a.cell_id = c.cell_id and c.ncell_id = b.cell_id and 1=1 and rownum <= 1000";
        if (vm.getCellName() != null && !"".equals(vm.getCellName())) {
            sql += " and a.cell_name = '" + vm.getCellName() + "'";
        }
        if (vm.getNcellName() != null && !"".equals(vm.getNcellName())) {
            sql += " and b.cell_name = '" + vm.getNcellName() + "'";
        }
        if (vm.getCellPci() != null && !"".equals(vm.getCellPci())) {
            sql += " and a.pci = '" + vm.getCellPci() + "'";
        }
        if (vm.getNcellPci() != null && !"".equals(vm.getNcellPci())) {
            sql += " and b.pci = '" + vm.getNcellPci() + "'";
        }
        Query query = entityManager.createNativeQuery(sql, NcellRelation.class);
        if (query == null) {
            return new ArrayList<NcellRelationDTO>();
        }
        List<NcellRelation> list = query.getResultList();
        List<NcellRelationDTO> dtoList = list.stream()
                                             .map(NcellRelationMapper.INSTANCE::ncellRelationToNcellRelationDTO)
                                             .collect(Collectors.toList());
        return dtoList;
    }
}
