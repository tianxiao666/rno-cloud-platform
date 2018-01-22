package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmStructAnalysisQueryMapper {
    /**
     * 结构优化分析任务查询
     * @param vm 结构优化任务vm
     * @return 结构优化分析任务查询DTO
     */
    List<GsmStructAnalysisJobDTO> taskQuery(GsmStructAnalysisQueryVM vm);
}
