package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GsmStructAnalysisQueryMapper {
    List<GsmStructAnalysisJobDTO> taskQuery(GsmStructAnalysisQueryVM vm);
}
