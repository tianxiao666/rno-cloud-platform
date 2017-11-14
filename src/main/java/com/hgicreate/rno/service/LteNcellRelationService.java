package com.hgicreate.rno.service;

import com.hgicreate.rno.dao.LteNcellRelationDao;
import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.service.dto.DataCollectDTO;
import com.hgicreate.rno.service.dto.LteNcellImportDtDTO;
import com.hgicreate.rno.service.dto.LteNcellImportFileDTO;
import com.hgicreate.rno.service.dto.LteNcellRelationDTO;
import com.hgicreate.rno.service.mapper.LteNcellImportFileMapper;
import com.hgicreate.rno.service.mapper.LteNcellRelationMapper;
import com.hgicreate.rno.web.rest.vm.LteNcellImportDtQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellImportQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellRelationQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteNcellRelationService {

    private final LteNcellRelationDao lteNcellRelationDao;

    private final OriginFileRepository originFileRepository;

    private final DataJobRepository dataJobRepository;

    public LteNcellRelationService(LteNcellRelationDao lteNcellRelationDao, OriginFileRepository originFileRepository, DataJobRepository dataJobRepository) {
        this.lteNcellRelationDao = lteNcellRelationDao;
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
    }

    public List<LteNcellRelationDTO> queryNcellRelationDTOs(LteNcellRelationQueryVM vm) {
        List<LteNcellRelationDTO> dtoList = lteNcellRelationDao.queryNcellRelation(vm)
                                               .stream()
                                               .map(LteNcellRelationMapper.INSTANCE::ncellRelationToNcellRelationDTO)
                                               .collect(Collectors.toList());
        return dtoList;
    }

    public  List<LteNcellImportFileDTO> queryImport(LteNcellImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBegUploadDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndUploadDate()+" 23:59:59");
        List<DataJob> list = new ArrayList<>();
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataType(area,
                    beginDate,endDate,"LTE-NCELL-RELATION-DATA");
        }else{
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataType(area,
                    vm.getStatus(),beginDate,endDate,"LTE-NCELL-RELATION-DATA");
        }


//        List<DataCollectDTO> dtoList = new ArrayList<>();
//        dtoList.add(new DataCollectDTO(1, "广州市", "2015-10-9 11:35:49",
//                "棠下小区邻区数据.csv", "1366322", "2015-10-9 11:36:09",
//                "2015-10-9 11:36:41", "liu.yp@iscreate.com", "部分失败"));
//        dtoList.add(new DataCollectDTO(2, "广州市", "2015-9-28 18:18:41",
//                "天河小区邻区数据.csv", "19490250", "2015-9-28 18:19:03",
//                "2015-9-28 18:19:35", "liu.yp@iscreate.com", "全部成功"));
        return list.stream()
                   .map(LteNcellImportFileMapper.INSTANCE::ncellImportFileToNcellImportFileDTO)
                   .collect(Collectors.toList());
    }

    public List<LteNcellImportDtDTO> queryImportDt(LteNcellImportDtQueryVM vm){
        List<LteNcellImportDtDTO> dtoList = new ArrayList<>();
        dtoList.add(new LteNcellImportDtDTO("广州市","地市邻区关系",
                "棠下小区邻区数据.csv","100","12","2015-10-9 11:35:49"));
        return dtoList;
    }
}
