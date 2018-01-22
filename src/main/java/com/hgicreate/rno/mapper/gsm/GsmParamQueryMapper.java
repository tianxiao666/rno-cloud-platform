package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmParamQueryMapper {

    /**
     * 查询区域下的所有bsc
     *
     * @param cityId
     * @return 符合条件的bsc
     */
    List<Map<String, Object>> queryBscListByCityId(int cityId);

    /**
     * 查询当前条件下的测量日期
     *
     * @param cityId
     * @param dataType 日期类型
     * @return 符合条件的测量日期
     */
    List<Map<String, Object>> queryDateListByCityId(int cityId, String dataType);

    /**
     * 获取符合条件的cell结果集
     *
     * @param vm 参数对象
     * @return cell list
     */
    List<Map<String, Object>> getCellParamRecord(GsmParamQueryVM vm);

    /**
     * 获取符合条件的channel结果集
     *
     * @param vm 参数对象
     * @return channel list
     */
    List<Map<String, Object>> getChannelParamRecord(GsmParamQueryVM vm);

    /**
     * 获取符合条件的ncell结果集
     *
     * @param vm 参数对象
     * @return ncell list
     */
    List<Map<String, Object>> getNcellParamRecord(GsmParamQueryVM vm);

}
