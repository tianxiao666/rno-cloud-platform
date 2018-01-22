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
     * @param vm 参数对象
     * @return cell比较结果lsit
     */
    List<Map<String, Object>> eriCellParamsCompare(GsmParamChangeVM vm);

    /**
     * channel参数比较
     *
     * @param vm 参数对象
     * @return channel比较结果list
     */
    List<Map<String, Object>> eriChannelParamsCompare(GsmParamChangeVM vm);

    /**
     * ncell参数比较
     *
     * @param vm 参数对象
     * @return ncell比较结果list
     */
    List<Map<String, Object>> eriNeighbourParamsCompare(GsmParamChangeVM vm);

    /**
     * 匹配cell日期数据数量
     *
     * @param vm 参数对象
     * @return 匹配到cell数量
     */
    Integer typeCellDataNumberOnTheDate(GsmParamChangeVM vm);

    /**
     * 匹配channel日期数据数量
     *
     * @param vm 参数对象
     * @return 匹配到的channel数量
     */
    Integer typeChannelDataNumberOnTheDate(GsmParamChangeVM vm);

    /**
     * 匹配ncell日期数据数量
     *
     * @param vm 参数对象
     * @return 匹配到的ncell数量
     */
    Integer typeNeighbourDataNumberOnTheDate(GsmParamChangeVM vm);

    /**
     * 获取bsc
     *
     * @param vm 参数对象
     * @return bsc集合list
     */
    List<LinkedHashMap<String, Object>> getBscById(GsmParamChangeVM vm);

    /**
     * cell参数差异详情
     *
     * @param vm 参数对象
     * @return cell差异详情list
     */
    List<Map<String, Object>> eriCellParamsDetail(GsmParamChangeVM vm);

    /**
     * channel参数差异详情
     *
     * @param vm 参数对象
     * @return channel差异详情list
     */
    List<Map<String, Object>> eriChannelParamsDetail(GsmParamChangeVM vm);

    /**
     * ncell参数差异详情
     *
     * @param vm 参数对象
     * @return ncell差异详情list
     */
    List<Map<String, Object>> eriNeighbourParamsDetail(GsmParamChangeVM vm);

    /**
     * cell参数差异导出
     *
     * @param vm 参数对象
     * @return cell参数差异对比结果list
     */
    List<Map<String, Object>> eriCellParamsCompareResult(GsmParamChangeVM vm);

    /**
     * channel参数差异导出
     *
     * @param vm 参数对象
     * @return  channel参数差异对比结果list
     */
    List<Map<String, Object>> eriChannelParamsCompareResult(GsmParamChangeVM vm);

    /**
     * ncell参数差异导出
     *
     * @param vm 参数对象
     * @return ncell参数差异对比结果list
     */
    List<Map<String, Object>> eriNeighbourParamsCompareResult(GsmParamChangeVM vm);

}
