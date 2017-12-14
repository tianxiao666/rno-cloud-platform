package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmTrafficQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GsmTrafficMapper {
    List<GsmTrafficQueryDTO> gsmTrafficQuery(GsmTrafficQueryVM vm);
}
