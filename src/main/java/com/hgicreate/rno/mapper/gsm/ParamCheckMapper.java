package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.ParamCheckVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ParamCheckMapper {
    //功率检查
    List<Map<String, Object>> getEriCellPowerCheckResult(ParamCheckVM vm);
    //跳频检查
    List<Map<String, Object>> getEriCellFreqHopCheckResultTrue(ParamCheckVM vm);
    List<Map<String, Object>> getEriCellFreqHopCheckResult(ParamCheckVM vm);
    //NCCPERM检查
    List<Map<String, Object>> getEriCellNccpermResult(ParamCheckVM vm);
    //测量频点多定义
    List<Map<String, Object>> getEriCellMeaFreqResult(ParamCheckVM vm);
    //BA表个数检查
    List<Map<String, Object>> getEriCellBaNumCheckResult(ParamCheckVM vm);
    //TALIM_MAXTA检查
    List<Map<String, Object>> getEriCellTalimAndMaxtaCheckResult(ParamCheckVM vm);
    //同频同bsic检查
    List<Map<String, Object>> getEriCellCoBsicCheckResult(ParamCheckVM vm);
    //邻区过多过少检查
    List<Map<String, Object>> getEriCellNcellNumCheckResult(ParamCheckVM vm);
    //本站邻区漏定义
    List<Map<String, Object>> getEriCellNcellMomitCheckResult(ParamCheckVM vm);
    //单向邻区检查
    List<Map<String, Object>> getEriCellUnidirNcellResult(ParamCheckVM vm);
    //同邻频检查
    List<Map<String, Object>> getEriCellSameNcellFreqData(ParamCheckVM vm);
}
