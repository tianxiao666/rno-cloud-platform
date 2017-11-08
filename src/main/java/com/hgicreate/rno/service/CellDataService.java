package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.repository.CellDataRepository;
import com.hgicreate.rno.service.dto.CellDataDTO;
import com.hgicreate.rno.service.mapper.CellDataMapper;
import com.hgicreate.rno.web.rest.vm.CellDataVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class CellDataService {

    private final CellDataRepository cellDataRepository;

    public CellDataService(CellDataRepository cellDataRepository) {
        this.cellDataRepository = cellDataRepository;
    }

    public List<CellDataDTO> queryCellByCondition(CellDataVM cellDataVM){
        List<CellDataDTO> cellDataDTOS= cellDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
           List<Predicate> predicates = new ArrayList<>();
           predicates.add(criteriaBuilder.equal(root.get("areaId"), cellDataVM.getCityId()));
            if(!cellDataVM.getCellId().trim().equals("")){
                predicates.add(criteriaBuilder.equal(root.get("cellId"), cellDataVM.getCellId()));
            }
            if(!cellDataVM.getCellName().trim().equals("")){
                predicates.add(criteriaBuilder.like(root.get("cellName"),"%"+cellDataVM.getCellName()+"%"));
            }
            if(!cellDataVM.getPci().trim().equals("")){
                predicates.add(criteriaBuilder.equal(root.get("pci"), cellDataVM.getPci()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }).stream().map(CellDataMapper.INSTANCE::cellToCellData).collect(Collectors.toList());
        if(cellDataDTOS.size() <1000){
            return cellDataDTOS;
        }else{
            return  cellDataDTOS.subList(0,1000);
        }
    }
}
