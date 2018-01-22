package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrAnalysisQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmMrrAnalysisMapper {
    /**
     * 根据areaId获取其所有bsc
     *
     * @param areaId
     * @return bsclist
     */
    List<Map<String, Object>> queryAllBscByAreaId(long areaId);

    /**
     * 根据查询条件集vm查询符合条件的mrr数据
     *
     * @param vm 查询条件对象
     * @return mrr数据list
     */
    List<Map<String, Object>> queryAllMrrData(GsmMrrAnalysisQueryVM vm);
}
