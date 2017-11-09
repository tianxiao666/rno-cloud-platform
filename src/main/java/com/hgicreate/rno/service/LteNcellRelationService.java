package com.hgicreate.rno.service;

import com.hgicreate.rno.dao.LteNcellRelationDao;
import com.hgicreate.rno.service.dto.DataCollectDTO;
import com.hgicreate.rno.service.dto.LteNcellRelationDTO;
import com.hgicreate.rno.service.mapper.LteNcellRelationMapper;
import com.hgicreate.rno.web.rest.vm.LteNcellImportQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellRelationQueryVM;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteNcellRelationService {

    private final LteNcellRelationDao lteNcellRelationDao;

    public LteNcellRelationService(LteNcellRelationDao lteNcellRelationDao) {
        this.lteNcellRelationDao = lteNcellRelationDao;
    }

    public List<LteNcellRelationDTO> queryNcellRelationDTOs(LteNcellRelationQueryVM vm) {
        List<LteNcellRelationDTO> dtoList = lteNcellRelationDao.queryNcellRelation(vm)
                                               .stream()
                                               .map(LteNcellRelationMapper.INSTANCE::ncellRelationToNcellRelationDTO)
                                               .collect(Collectors.toList());
        return dtoList;
    }

    public  List<DataCollectDTO> queryImport(LteNcellImportQueryVM vm){
        List<DataCollectDTO> dtoList = new ArrayList<>();
        dtoList.add(new DataCollectDTO(1, "广州市", "2015-10-9 11:35:49",
                "棠下小区邻区数据.csv", "1366322", "2015-10-9 11:36:09",
                "2015-10-9 11:36:41", "liu.yp@iscreate.com", "部分失败"));
        dtoList.add(new DataCollectDTO(2, "广州市", "2015-9-28 18:18:41",
                "天河小区邻区数据.csv", "19490250", "2015-9-28 18:19:03",
                "2015-9-28 18:19:35", "liu.yp@iscreate.com", "全部成功"));
        return dtoList;
    }
}
