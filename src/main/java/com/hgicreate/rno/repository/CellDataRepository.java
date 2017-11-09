package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.web.rest.vm.CellDataVM;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellDataRepository extends JpaRepository<Cell, String> {

    List<Cell> findByEnodebId(String enodebId);

}
