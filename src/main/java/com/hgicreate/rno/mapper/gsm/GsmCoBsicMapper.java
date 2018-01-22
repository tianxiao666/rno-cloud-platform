package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicQueryVM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yang.ch1
 */
@Mapper
public interface GsmCoBsicMapper {

    /**
     * 通过区域id获取co-bsic小区
     * @param vm co-bsic查询条件对象
     * @return co-bsic小区集合
     */
    List<Map<String,Object>> getCoBsicCellsByAreaId(GsmCoBsicQueryVM vm);

    /**
     * 通过bcch和区域id查询co-bsic小区
     * @param vm co-bsic查询条件对象
     * @return co-bsic小区集合
     */
    List<Map<String, Object>> getCoBsicCellsByAreaIdAndBcch(GsmCoBsicQueryVM vm);

    /**
     * 查询源小区和目标小区的共同邻区
     * @param sourceCell 源小区
     * @param targetCell 目标小区
     * @return 邻区关系集合
     */
    List<GsmNcellRelation> queryCommonNcellByTwoCell(@Param("sourceCell") String sourceCell, @Param("targetCell") String targetCell);

    /**
     * 查询源小区和目标小区是否是邻区关系，如果是，则返回邻区关系对象集合
     * @param sourceCell 源小区
     * @param targetCell 目标小区
     * @return 邻区关系集合
     */
    List<GsmNcellRelation> queryNcell(@Param("sourceCell") String sourceCell, @Param("targetCell")String targetCell);

    /**
     * 查询源小区和目标小区的经纬度
     * @param sourceCell 源小区
     * @param targetCell 目标小区
     * @return 经纬度集合
     */
    List<String> getLonLatsByCells(@Param("sourceCell") String sourceCell,  @Param("targetCell")String targetCell);

    /**
     *  通过区域名称查询gsm表中area_id
     * @param areaName 区域名称
     * @return 区域id
     */
    Integer findGsmAreaIdByName(@Param("areaName") String areaName);

    /**
     * 通过城市id和配置条目名称查询co-bsic配置信息
     * @param cityId 区域id
     * @param schemaName 配置文件名称
     * @return co-bsic配置信息
     */
    List<Map<String,Object>> queryCoBsicConfigSchema(@Param("cityId") int cityId,@Param("schemaName") String schemaName);

    /**
     * 通过配置信息id获取配置信息
     * @param ids 被选中配置信息id集
     * @return 配置信息列表
     */
    List<Map<String, Object>> queryConfigSchemaById(@Param("ids")String ids);
}
