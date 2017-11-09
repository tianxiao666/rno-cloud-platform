package com.hgicreate.rno.service;

import com.hgicreate.rno.dao.LteCellDataDao;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional
public class LteCellDataService {

    private final LteCellDataDao lteCellDataDao;

    public LteCellDataService( LteCellDataDao lteCellDataDao) {
        this.lteCellDataDao = lteCellDataDao;
    }

    public List<LteCellDataDTO> queryLteCell(LteCellDataVM lteCellDataVM){
       return  lteCellDataDao.queryLteCellByCondition(lteCellDataVM);
    }
}
