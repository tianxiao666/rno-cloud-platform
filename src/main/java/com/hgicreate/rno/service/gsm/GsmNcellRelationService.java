package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.mapper.gsm.GsmNcellRelationQueryMapper;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.GsmNcellDescRepository;
import com.hgicreate.rno.service.gsm.dto.GsmNcellDescDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcellImportFileDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcellRelationDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmNcellDescMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmNcellImportFileMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmNcellRelationMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcellImportDtQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcellImportQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcellRelationQueryVM;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tao.xj
 */
@Service
public class GsmNcellRelationService {

    private final GsmNcellRelationQueryMapper gsmNcellRelationQueryMapper;
    private final DataJobRepository dataJobRepository;
    private final GsmNcellDescRepository gsmNcellDescRepository;

    public GsmNcellRelationService(GsmNcellRelationQueryMapper gsmNcellRelationQueryMapper,
                                   DataJobRepository dataJobRepository, GsmNcellDescRepository gsmNcellDescRepository) {
        this.gsmNcellRelationQueryMapper = gsmNcellRelationQueryMapper;
        this.dataJobRepository = dataJobRepository;
        this.gsmNcellDescRepository = gsmNcellDescRepository;
    }

    public List<GsmNcellRelationDTO> queryNcellRelationDTOs(GsmNcellRelationQueryVM vm) {
        return gsmNcellRelationQueryMapper.queryNcellRelation(vm)
                .stream()
                .map(GsmNcellRelationMapper.INSTANCE::ncellToNcellDTO)
                .collect(Collectors.toList());
    }

    public List<GsmNcellImportFileDTO> queryImport(GsmNcellImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegUploadDate() + " 00:00:00");
        Date endDate = sdf.parse(vm.getEndUploadDate() + " 23:59:59");
        List<DataJob> list;
        if ("全部".equals(vm.getStatus())) {
            list = dataJobRepository
                    .findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                            area, beginDate, endDate, "GSM-NCELL-RELATION");
        } else {
            list = dataJobRepository
                    .findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                            area, vm.getStatus(), beginDate, endDate, "GSM-NCELL-RELATION");
        }
        return list.stream()
                .map(GsmNcellImportFileMapper.INSTANCE::ncellImportFileToNcellImportFileDTO)
                .collect(Collectors.toList());
    }

    public List<GsmNcellDescDTO> queryImportDt(GsmNcellImportDtQueryVM vm) {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        List<GsmNcellDescDTO> dtoList;
        if ("全部".equals(vm.getDataType())) {
            dtoList = gsmNcellDescRepository.findTop1000ByAreaOrderByCreatedDateDesc(area)
                    .stream()
                    .map(GsmNcellDescMapper.INSTANCE::ncellDescToNcellDescDTO)
                    .collect(Collectors.toList());
        } else {
            dtoList = gsmNcellDescRepository.findTop1000ByAreaAndDataTypeOrderByCreatedDateDesc(area, vm.getDataType())
                    .stream()
                    .map(GsmNcellDescMapper.INSTANCE::ncellDescToNcellDescDTO)
                    .collect(Collectors.toList());
        }
        return dtoList;
    }
}
