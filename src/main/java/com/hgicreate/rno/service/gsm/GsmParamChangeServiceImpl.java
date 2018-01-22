package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmParamChangeMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zeng.dh1
 */

@Service
public class GsmParamChangeServiceImpl implements GsmParamChangeService {

    private final GsmParamChangeMapper gsmParamChangeMapper;

    public GsmParamChangeServiceImpl(GsmParamChangeMapper gsmParamChangeMapper) {
        this.gsmParamChangeMapper = gsmParamChangeMapper;
    }

    @Override
    public List<Map<String, Object>> changeParamData(GsmParamChangeVM vm) {
        String sumSql = "", caseSql = "", paramsSql = "";
        String[] params = vm.getParamStr().split(",");
        for (String param : params) {
            //TO改为“TO”
            if (("TO").equals(param)) {
                sumSql += "sum(\"" + param + "\") as \"" + param + "\",";
                caseSql += "case when t1.\"" + param + "\"<> t2.\"" + param + "\" then 1 else 0 end as \"" + param + "\",";
            } else {
                sumSql += "sum(" + param + ") as " + param + ",";
                caseSql += "case when t1." + param + "<> t2." + param + " then 1 else 0 end as " + param + ",";
            }
            //TO改为“TO”
            if (("TO").equals(param)) {
                paramsSql += "\"TO\"" + ",";
            }
            //BCCH改为BCCHNO
            else if (("BCCH").equals(param)) {
                paramsSql += "BCCHNO as BCCH" + ",";
            }
            //BSIC改为NCC||BCC as BSIC
            else if (("BSIC").equals(param)) {
                paramsSql += " NCC||BCC as BSIC" + ",";
            }
            //ACC改为ACC_16
            else if (("ACC").equals(param)) {
                paramsSql += " ACC_16 as ACC" + ",";
            }
            //NCCPERM改为NCCPERM_8
            else if (("NCCPERM").equals(param)) {
                paramsSql += " NCCPERM_8 as NCCPERM" + ",";
            }
            //ACTIVEMBCCHNO改为ACTIVE_32
            else if (("ACTIVEMBCCHNO").equals(param)) {
                paramsSql += " ACTIVE_32 as ACTIVEMBCCHNO" + ",";
            }
            //IDLEMBCCHNO改为IDLE_32
            else if (("IDLEMBCCHNO").equals(param)) {
                paramsSql += " IDLE_32 as IDLEMBCCHNO" + ",";
            }
            /**Channel**/
            //MAIO改为MAIO_16
            else if (("MAIO").equals(param)) {
                paramsSql += " MAIO_16 as MAIO" + ",";
            }
            //MAIO改为MAIO_16
            else if (("ETCHTN").equals(param)) {
                paramsSql += " ETCHTN_8 as ETCHTN" + ",";
            }
            else {
                paramsSql += param + ",";
            }
        }
        vm.setSumSql(sumSql.substring(0, sumSql.length() - 1));
        vm.setCaseSql(caseSql.substring(0, caseSql.length() - 1));
        vm.setParamsSql(paramsSql.substring(0, paramsSql.length() - 1));

        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

        if (("cell").equals(vm.getParamType())) {
            res = gsmParamChangeMapper.eriCellParamsCompare(vm);
        } else if (("channel").equals(vm.getParamType())) {
            res = gsmParamChangeMapper.eriChannelParamsCompare(vm);
        } else if (("neighbour").equals(vm.getParamType())) {
            res = gsmParamChangeMapper.eriNeighbourParamsCompare(vm);
        }

        //结果加入BSC名称
        List<LinkedHashMap<String, Object>> bscList = gsmParamChangeMapper.getBscById(vm);
        String bscIdFromBsc = "", bscIdFromRes = "", engName = "";
        for (Map<String, Object> map : res) {
            if (map.get("BSC") != null) {
                bscIdFromRes = map.get("BSC").toString();
            }
            for (Map<String, Object> bsc : bscList) {
                if (bsc.get("ID") != null) {
                    bscIdFromBsc = bsc.get("ID").toString();
                }
                if (bscIdFromRes.equals(bscIdFromBsc)) {
                    if (bsc.get("BSC") != null) {
                        engName = bsc.get("BSC").toString();
                    }
                }
            }
            map.put("BSC_ENGNAME", engName);
        }
        //按照BSC名称排序
        List<String> bscNameList = new ArrayList<String>();
        for (Map<String, Object> map : res) {
            bscNameList.add(map.get("BSC_ENGNAME").toString());
        }
        Collections.sort(bscNameList);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (String bscName : bscNameList) {
            for (Map<String, Object> map : res) {
                if (bscName.equals(map.get("BSC_ENGNAME").toString())) {
                    result.add(map);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> exportChangeParamData(GsmParamChangeVM vm) {
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> resSize = new ArrayList<Map<String, Object>>();

        String paramsTot = "", params1 = "", params2 = "", whereSql = "";
        String[] params = vm.getParamStr().split(",");
        for (String param : params) {
            paramsTot += " " + param + "_d1," + param + "_d2,";
            whereSql += " " + param + "_d1<>" + param + "_d2 or";
            //TO改为“TO”
            if (("TO").equals(param)) {
                params1 += " \"TO\"" + " as TO_d1,";
                params2 += " \"TO\"" + " as TO_d2,";
            }
            //BCCH改为BCCHNO
            else if (("BCCH").equals(param)) {
                params1 += " BCCHNO as BCCH_d1" + ",";
                params2 += " BCCHNO as BCCH_d2" + ",";
            }
            //BSIC改为NCC||BCC as BSIC
            else if (("BSIC").equals(param)) {
                params1 += " NCC||BCC as BSIC_d1" + ",";
                params2 += " NCC||BCC as BSIC_d2" + ",";
            }
            //ACC改为ACC_16
            else if (("ACC").equals(param)) {
                params1 += " ACC_16 as ACC_d1" + ",";
                params2 += " ACC_16 as ACC_d2" + ",";
            }
            //NCCPERM改为NCCPERM_8
            else if (("NCCPERM").equals(param)) {
                params1 += " NCCPERM_8 as NCCPERM_d1" + ",";
                params2 += " NCCPERM_8 as NCCPERM_d2" + ",";
            }
            //ACTIVEMBCCHNO改为ACTIVE_32
            else if (("ACTIVEMBCCHNO").equals(param)) {
                params1 += " ACTIVE_32 as ACTIVEMBCCHNO_d1" + ",";
                params2 += " ACTIVE_32 as ACTIVEMBCCHNO_d2" + ",";
            }
            //IDLEMBCCHNO改为IDLE_32
            else if (("IDLEMBCCHNO").equals(param)) {
                params1 += " IDLE_32 as IDLEMBCCHNO_d1" + ",";
                params2 += " IDLE_32 as IDLEMBCCHNO_d2" + ",";
            }
            /**Channel**/
            //MAIO改为MAIO_16
            else if (("MAIO").equals(param)) {
                params1 += " MAIO_16 as MAIO_d1" + ",";
                params2 += " MAIO_16 as MAIO_d2" + ",";
            }
            //ETCHTN改为ETCHTN_8
            else if (("ETCHTN").equals(param)) {
                params1 += " ETCHTN_8 as ETCHTN_d1" + ",";
                params2 += " ETCHTN_8 as ETCHTN_d2" + ",";
            } else {
                params1 += " " + param + " as " + param + "_d1,";
                params2 += " " + param + " as " + param + "_d2,";
            }
        }
        vm.setParams1(params1.substring(0, params1.length() - 1));
        vm.setParams2(params2.substring(0, params2.length() - 1));
        vm.setParamsTot(paramsTot.substring(0, paramsTot.length() - 1));
        //去掉or
        vm.setWhereSql(whereSql.substring(0, whereSql.length() - 2));

        if (("cell").equals(vm.getParamType())) {
            resSize = gsmParamChangeMapper.eriCellParamsCompareResult(vm);
        } else if (("channel").equals(vm.getParamType())) {
            resSize = gsmParamChangeMapper.eriChannelParamsCompareResult(vm);
        } else if (("neighbour").equals(vm.getParamType())) {
            resSize = gsmParamChangeMapper.eriNeighbourParamsCompareResult(vm);
        }
        if (resSize.size() == 0) {
            return resSize;
        } else {
            List<Map<String, Object>> resData = new ArrayList<Map<String, Object>>();
            String p1 = "", p2 = "";
            Map<String, Object> map = null;
            for (Map<String, Object> one : resSize) {
                for (String param : params) {
                    if (one.get(param + "_D1") == null || one.get(param + "_D2") == null) {
                        continue;
                    }
                    p1 = one.get(param + "_D1").toString();
                    p2 = one.get(param + "_D2").toString();
                    System.out.println(p1+"="+p2);
                    if (!(p1).equals(p2)) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC").toString());
                        map.put("CELL", one.get("CELL").toString());
                        if (("channel").equals(vm.getParamType())) {
                            map.put("CHGR", one.get("CH_GROUP").toString());
                        } else if (("neighbour").equals(vm.getParamType())) {
                            map.put("N_CELL", one.get("N_CELL").toString());
                        }
                        map.put("PNAME", param);
                        map.put("PARAM1", p1);
                        map.put("PARAM2", p2);
                        resData.add(map);
                    }
                }
            }
            return resData;
        }
    }

    @Override
    public List<Map<String, Object>> queryParamDeatialData(GsmParamChangeVM vm) {
        //TO改为“TO”
        if (("TO").equals(vm.getParam())) {
            vm.setParam("\"TO\"");
        }
        //BCCH改为BCCHNO
        if (("BCCH").equals(vm.getParam())) {
            vm.setParam("BCCHNO");
        }
        //BSIC改为NCC||BCC as BSIC
        if (("BSIC").equals(vm.getParam())) {
            vm.setParam("NCC||BCC");
        }
        //ACC改为ACC_16
        if (("ACC").equals(vm.getParam())) {
            vm.setParam("ACC_16");
        }
        //NCCPERM改为NCCPERM_8
        if (("NCCPERM").equals(vm.getParam())) {
            vm.setParam("NCCPERM_8");
        }
        //ACTIVEMBCCHNO改为ACTIVE_32
        if (("ACTIVEMBCCHNO").equals(vm.getParam())) {
            vm.setParam("ACTIVE_32");
        }
        //IDLEMBCCHNO改为IDLE_32
        if (("IDLEMBCCHNO").equals(vm.getParam())) {
            vm.setParam("IDLE_32");
        }
        //MAIO改为MAIO_16
        if (("MAIO").equals(vm.getParam())) {
            vm.setParam("MAIO_16");
        }
        //ETCHTN改为ETCHTN_8
        if (("ETCHTN").equals(vm.getParam())) {
            vm.setParam("ETCHTN_8");
        }
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        if (("cell").equals(vm.getParamType())) {
            res = gsmParamChangeMapper.eriCellParamsDetail(vm);
        } else if (("channel").equals(vm.getParamType())) {
            res = gsmParamChangeMapper.eriChannelParamsDetail(vm);
        } else if (("neighbour").equals(vm.getParamType())) {
            res = gsmParamChangeMapper.eriNeighbourParamsDetail(vm);
        }
        return res;
    }

    @Override
    public Boolean queryDateExist(GsmParamChangeVM vm) {
        Boolean flag = true;
        Integer count = 0;
        if ("cell".equals(vm.getParamType())) {
            count = gsmParamChangeMapper.typeCellDataNumberOnTheDate(vm);
        } else if ("channel".equals(vm.getParamType())) {
            count = gsmParamChangeMapper.typeChannelDataNumberOnTheDate(vm);
        } else if ("neighbour".equals(vm.getParamType())) {
            count = gsmParamChangeMapper.typeNeighbourDataNumberOnTheDate(vm);
        }
        if (count == 0) {
            flag = false;
        }
        return flag;
    }
}
