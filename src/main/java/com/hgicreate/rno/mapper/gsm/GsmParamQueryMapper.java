package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmParamQueryMapper {
    //功率检查
    List<Map<String, Object>> queryBscListByCityId(int cityId);
    //跳频检查
    List<Map<String, Object>> queryDateListByCityId(int cityId, String dataType);
    /*List<Map<String, Object>> getEriCellFreqHopCheckResult(GsmParamCheckVM vm);
    //NCCPERM检查
    List<Map<String, Object>> getEriCellNccpermResult(GsmParamCheckVM vm);
    //测量频点多定义
    List<Map<String, Object>> getEriCellMeaFreqResult(GsmParamCheckVM vm);
    //BA表个数检查
    List<Map<String, Object>> getEriCellBaNumCheckResult(GsmParamCheckVM vm);
    //TALIM_MAXTA检查
    List<Map<String, Object>> getEriCellTalimAndMaxtaCheckResult(GsmParamCheckVM vm);
    //同频同bsic检查
    List<Map<String, Object>> getEriCellCoBsicCheckResult(GsmParamCheckVM vm);
    //邻区过多过少检查
    List<Map<String, Object>> getEriCellNcellNumCheckResult(GsmParamCheckVM vm);
    //本站邻区漏定义
    List<Map<String, Object>> getEriCellNcellMomitCheckResult(GsmParamCheckVM vm);
    //单向邻区检查
    List<Map<String, Object>> getEriCellUnidirNcellResult(GsmParamCheckVM vm);
    //同邻频检查
    List<Map<String, Object>> getEriCellSameNcellFreqData(GsmParamCheckVM vm);*/
}
