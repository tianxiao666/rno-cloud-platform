package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.ParamCheckMapper;
import com.hgicreate.rno.web.rest.gsm.vm.ParamCheckVM;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ParamCheckServiceImpl implements ParamCheckService{

    private final ParamCheckMapper paramCheckMapper;

    public ParamCheckServiceImpl(ParamCheckMapper paramCheckMapper) {
        this.paramCheckMapper = paramCheckMapper;
    }

    public List<Map<String, Object>> queryParamData(ParamCheckVM vm) {
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        vm.setBscStr(vm.getBscStr().substring(0,vm.getBscStr().length()));
        //功率检查
        if (("powerCheck").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellPowerCheckResult(vm);
        }
        //跳频检查
        if (("freqHopCheck").equals(vm.getCheckType())) {
            if(vm.getCheckMaxChgr()==true) {
                result = paramCheckMapper.getEriCellFreqHopCheckResultTrue(vm);
            }else  {
                result = paramCheckMapper.getEriCellFreqHopCheckResult(vm);
            }
        }
        //NCCPERM检查
        if (("nccperm").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellNccpermResult(vm);
        }
        //测量频点多定义
        if (("meaFreqMultidefined").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = paramCheckMapper.getEriCellMeaFreqResult(vm);
            result = getEriCellMeaFreqMultidefineResult(res);
        }
        //测量频点漏定义
        if (("meaFreqMomit").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = paramCheckMapper.getEriCellMeaFreqResult(vm);
            result = getEriCellMeaFreqMomitResult(res);
        }
        //BA表个数检查
        if (("baNumCheck").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellBaNumCheckResult(vm);
        }
        //TALIM_MAXTA检查
        if (("talimMaxTa").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellTalimAndMaxtaCheckResult(vm);
        }
        //同频同bsic检查
        if (("sameFreqBsicCheck").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellCoBsicCheckResult(vm);
        }
        //邻区过多过少检查
        if (("ncellNumCheck").equals(vm.getCheckType())) {
            if(vm.getCheckNcellNum()==false) {
                vm.setNcellMaxNum(32);
                vm.setNcellMinNum(2);
            }
            result = paramCheckMapper.getEriCellNcellNumCheckResult(vm);
        }
        //本站邻区漏定义
        if (("ncellMomit").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellNcellMomitCheckResult(vm);
        }
        //单向邻区检查
        if (("unidirNcell").equals(vm.getCheckType())) {
            result = paramCheckMapper.getEriCellUnidirNcellResult(vm);
        }
        /*//同邻频检查
        if (("sameNcellFreqCheck").equals(vm.getCheckType())) {
            result = getEriCellSameNcellFreqCheckResult(bscIdStr, date, cityId, settings);
        }
        //邻区数据检查
        if (("ncellDataCheck").equals(vm.getCheckType())) {
            //result = getEriCellNcellDataCheckResult(bscIdStr, date, cityId, settings);
        }*/
        return result;
    }

    /**
     * 获取爱立信小区测量频点多定义结果
     */
    private List<Map<String, Object>> getEriCellMeaFreqMultidefineResult(List<Map<String, Object>> res) {

        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

        String activeStr = "",idleStr = "",ncellBcchStr = "",actives[] = {},idles[] = {},ncellBcchs[] = {};

        List<String> ncellBcchList;

        String overActiveStr = "",overActiveComm = "",overIdleStr = "",overIdleComm = "",command = "";

        Map<String,Object> map = null;

        //判断active与idle是否多定义
        for (Map<String, Object> one : res) {
            //初始化
            overActiveStr = "";
            overActiveComm = "";
            overIdleStr = "";
            overIdleComm = "";

            if (one.get("ACTIVE") != null && one.get("IDLE") != null
                    && one.get("NCELL_BCCH") != null) {
                activeStr = one.get("ACTIVE").toString();
                idleStr = one.get("IDLE").toString();
                ncellBcchStr = one.get("NCELL_BCCH").toString();
                actives = activeStr.split(",");
                idles = idleStr.split(",");
                ncellBcchs = ncellBcchStr.split(",");
                //转为list
                ncellBcchList = Arrays.asList(ncellBcchs);
                //判断active是否多定义
                for (String active : actives) {
                    if(!ncellBcchList.contains(active)) {
                        overActiveStr += active + ",";
                        overActiveComm += active + "&";
                    }
                }
                if(!("").equals(overActiveStr) && !("").equals(overActiveComm)) {
                    overActiveStr = overActiveStr.substring(0,overActiveStr.length()-1);
                    overActiveComm = overActiveComm.substring(0,overActiveComm.length()-1);
                    if(one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "ACTIVE");
                        map.put("OVER_DEFINE", overActiveStr);
                        command = "RLMFC:CELL="+one.get("CELL").toString()
                                +",MBCCHNO="+overActiveComm+",MRNIC,LISTTYPE=ACTIVE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
                //判断idle是否多定义
                for (String idle : idles) {
                    if(!ncellBcchList.contains(idle)) {
                        overIdleStr += idle + ",";
                        overIdleComm += idle + "&";
                    }
                }
                if(!("").equals(overIdleStr) && !("").equals(overIdleComm)) {
                    overIdleStr = overIdleStr.substring(0,overIdleStr.length()-1);
                    overIdleComm = overIdleComm.substring(0,overIdleComm.length()-1);
                    if(one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "IDLE");
                        map.put("OVER_DEFINE", overIdleStr);
                        command = "RLMFC:CELL="+one.get("CELL").toString()
                                +",MBCCHNO="+overIdleComm+",MRNIC,LISTTYPE=IDLE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
            }
        }
        return result;
    }
    /**
     * 获取爱立信小区测量频点漏定义结果
     */
    private List<Map<String, Object>> getEriCellMeaFreqMomitResult(List<Map<String, Object>> res) {
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

        String activeStr = "",idleStr = "",ncellBcchStr = "";

        String actives[] = {},idles[] = {},ncellBcchs[] = {};

        List<String> activeList;
        List<String> idleList;

        String leakActiveStr = "",leakActiveComm = "",leakIdleStr = "",leakIdleComm = "",command = "";

        Map<String,Object> map = null;

        //判断active与idle是否多定义
        for (Map<String, Object> one : res) {
            //初始化
            leakActiveStr = "";
            leakActiveComm = "";
            leakIdleStr = "";
            leakIdleComm = "";

            if (one.get("ACTIVE") != null && one.get("IDLE") != null
                    && one.get("NCELL_BCCH") != null) {
                activeStr = one.get("ACTIVE").toString();
                idleStr = one.get("IDLE").toString();
                ncellBcchStr = one.get("NCELL_BCCH").toString();
                actives = activeStr.split(",");
                idles = idleStr.split(",");
                ncellBcchs = ncellBcchStr.split(",");
                //转为list
                activeList = Arrays.asList(actives);
                idleList =  Arrays.asList(idles);
                //判断active是否多定义
                for (String bcch : ncellBcchs) {
                    if(!activeList.contains(bcch)) {
                        leakActiveStr += bcch + ",";
                        leakActiveComm += bcch + "&";
                    }
                }
                if(!("").equals(leakActiveStr) && !("").equals(leakActiveComm)) {
                    leakActiveStr = leakActiveStr.substring(0,leakActiveStr.length()-1);
                    leakActiveComm = leakActiveComm.substring(0,leakActiveComm.length()-1);
                    if(one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "ACTIVE");
                        map.put("LEAK_DEFINE", leakActiveStr);
                        command = "RLMFC:CELL="+one.get("CELL").toString()
                                +",MBCCHNO="+leakActiveComm+",MRNIC,LISTTYPE=ACTIVE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
                //判断idle是否多定义
                for (String bcch : ncellBcchs) {
                    if(!idleList.contains(bcch)) {
                        leakIdleStr += bcch + ",";
                        leakIdleComm += bcch + "&";
                    }
                }
                if(!("").equals(leakIdleStr) && !("").equals(leakIdleComm)) {
                    leakIdleStr = leakIdleStr.substring(0,leakIdleStr.length()-1);
                    leakIdleComm = leakIdleComm.substring(0,leakIdleComm.length()-1);
                    if(one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "IDLE");
                        map.put("LEAK_DEFINE", leakIdleStr);
                        command = "RLMFC:CELL="+one.get("CELL").toString()
                                +",MBCCHNO="+leakIdleComm+",MRNIC,LISTTYPE=IDLE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
            }
        }
        return result;
    }

}
