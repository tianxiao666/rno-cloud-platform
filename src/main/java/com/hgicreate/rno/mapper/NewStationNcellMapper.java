package com.hgicreate.rno.mapper;

import com.hgicreate.rno.service.dto.NewStationNcellImportQueryDTO;
import com.hgicreate.rno.web.rest.vm.NewStationNcellImportQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NewStationNcellMapper {
    List<NewStationNcellImportQueryDTO> queryImport(NewStationNcellImportQueryVM vm);
}
