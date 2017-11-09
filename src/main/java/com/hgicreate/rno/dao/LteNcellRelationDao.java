package com.hgicreate.rno.dao;

import com.hgicreate.rno.domain.NcellRelation;
import com.hgicreate.rno.web.rest.vm.LteNcellRelationQueryVM;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LteNcellRelationDao {
    private final EntityManager entityManager;

    public LteNcellRelationDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<NcellRelation> queryNcellRelation(LteNcellRelationQueryVM vm){
        String sql = "select c.id,a.cell_id as cell_id, a.cell_name as cell_name,a.enodeb_id as cell_Enodeb_Id,a.pci as cell_Pci," +
                "b.cell_id as ncell_Id,b.cell_name as ncell_Name,b.enodeb_id as ncell_Enodeb_Id,b.pci as ncell_Pci " +
                "from rno_lte_cell a,rno_lte_cell b,rno_lte_ncell_relation c " +
                "where a.cell_id = c.cell_id and c.ncell_id = b.cell_id and rownum <= 1000";
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
            return new ArrayList<NcellRelation>();
        }
        return query.getResultList();
    }
}
