package com.hgicreate.rno.dao;

import com.hgicreate.rno.web.rest.vm.LteCellDataImportVM;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class LteCellDataImportRecordDao {

    private EntityManager entityManager;

    public LteCellDataImportRecordDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<?> queryLteCellImportRecord(LteCellDataImportVM lteCellDataImportVM){
        String sql = "";

        return entityManager.createNativeQuery(sql).getResultList();
    }
}
