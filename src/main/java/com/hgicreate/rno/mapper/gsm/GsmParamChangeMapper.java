package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmParamChangeMapper {
    /**
     * cell参数比较
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriCellParamsCompare(GsmParamChangeVM vm);

    /**
     * channel参数比较
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriChannelParamsCompare(GsmParamChangeVM vm);

    /**
     * ncell参数比较
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriNeighbourParamsCompare(GsmParamChangeVM vm);

    /**
     * 匹配cell日期数据数量
     *
     * @param vm
     * @return
     */
    Integer typeCellDataNumberOnTheDate(GsmParamChangeVM vm);

    /**
     * 匹配channel日期数据数量
     *
     * @param vm
     * @return
     */
    Integer typeChannelDataNumberOnTheDate(GsmParamChangeVM vm);

    /**
     * 匹配ncell日期数据数量
     *
     * @param vm
     * @return
     */
    Integer typeNeighbourDataNumberOnTheDate(GsmParamChangeVM vm);

    /**
     * 获取bsc
     *
     * @param vm
     * @return
     */
    List<LinkedHashMap<String, Object>> getBscById(GsmParamChangeVM vm);

    /**
     * cell参数差异详情
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriCellParamsDetail(GsmParamChangeVM vm);

    /**
     * channel参数差异详情
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriChannelParamsDetail(GsmParamChangeVM vm);

    /**
     * ncell参数差异详情
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriNeighbourParamsDetail(GsmParamChangeVM vm);

    /**
     * cell参数差异导出
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriCellParamsCompareResult(GsmParamChangeVM vm);

    /**
     * channel参数差异导出
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriChannelParamsCompareResult(GsmParamChangeVM vm);

    /**
     * ncell参数差异导出
     *
     * @param vm
     * @return
     */
    List<Map<String, Object>> eriNeighbourParamsCompareResult(GsmParamChangeVM vm);

}
