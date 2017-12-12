package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmParamCheckMapper;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsDTO;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsExpandDTO;
import com.hgicreate.rno.util.LatLngHelperUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GsmParamCheckServiceImpl implements GsmParamCheckService {

    private final GsmParamCheckMapper gsmParamCheckMapper;

    public GsmParamCheckServiceImpl(GsmParamCheckMapper gsmParamCheckMapper) {
        this.gsmParamCheckMapper = gsmParamCheckMapper;
    }

    public List<Map<String, Object>> checkParamData(GsmParamCheckVM vm) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        //功率检查
        if (("powerCheck").equals(vm.getCheckType())) {
            result = gsmParamCheckMapper.getEriCellPowerCheckResult(vm);
        }
        //跳频检查
        if (("freqHopCheck").equals(vm.getCheckType())) {
            if (vm.getCheckMaxChgr() == true) {
                result = gsmParamCheckMapper.getEriCellFreqHopCheckResultTrue(vm);
            } else {
                result = gsmParamCheckMapper.getEriCellFreqHopCheckResult(vm);
            }
        }
        //NCCPERM检查
        if (("nccperm").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = gsmParamCheckMapper.getEriCellNccpermResult(vm);
            result = getEriCellNccpermFinalResult(res);
        }
        //测量频点多定义
        if (("meaFreqMultidefined").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = gsmParamCheckMapper.getEriCellMeaFreqResult(vm);
            result = getEriCellMeaFreqMultidefineResult(res);
        }
        //测量频点漏定义
        if (("meaFreqMomit").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = gsmParamCheckMapper.getEriCellMeaFreqResult(vm);
            result = getEriCellMeaFreqMomitResult(res);
        }
        //BA表个数检查
        if (("baNumCheck").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = gsmParamCheckMapper.getEriCellBaNumCheckResult(vm);
            result = getEriCellBaNumCheckFinalResult(res, vm);
        }
        //TALIM_MAXTA检查
        if (("talimMaxTa").equals(vm.getCheckType())) {
            result = gsmParamCheckMapper.getEriCellTalimAndMaxtaCheckResult(vm);
        }
        //同频同bsic检查
        if (("sameFreqBsicCheck").equals(vm.getCheckType())) {
            result = getEriCellCoBsicCheckFinalResult(vm);
        }
        //邻区过多过少检查
        if (("ncellNumCheck").equals(vm.getCheckType())) {
            if (vm.getCheckNcellNum() == false) {
                vm.setNcellMaxNum(32);
                vm.setNcellMinNum(2);
            }
            result = gsmParamCheckMapper.getEriCellNcellNumCheckResult(vm);
        }
        //本站邻区漏定义
        if (("ncellMomit").equals(vm.getCheckType())) {
            result = gsmParamCheckMapper.getEriCellNcellMomitCheckResult(vm);
        }
        //单向邻区检查
        if (("unidirNcell").equals(vm.getCheckType())) {
            result = gsmParamCheckMapper.getEriCellUnidirNcellResult(vm);
        }
        //同邻频检查
        if (("sameNcellFreqCheck").equals(vm.getCheckType())) {
            List<Map<String, Object>> res = gsmParamCheckMapper.getEriCellSameNcellFreqData(vm);
            result = getEriCellSameNcellFreqCheckResult(res);
        }
        //邻区数据检查
        if (("ncellDataCheck").equals(vm.getCheckType())) {
            //result = getEriCellNcellDataCheckResult(bscIdStr, date, cityId, settings);
        }
        return result;
    }

    /**
     * 获取BA表个数检查结果
     */
    private List<Map<String, Object>> getEriCellBaNumCheckFinalResult(List<Map<String, Object>> res, GsmParamCheckVM vm) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> baMap;

        String activeStr = "", idleStr = "", bscStr = "", cellStr = "";

        if (vm.getCheckBaNum() == false) {
            vm.setMaxNum(20);
            vm.setMinNum(5);
        }
        for (Map<String, Object> one : res) {
            if (one.get("ACTIVE") != null && one.get("IDLE") != null) {
                activeStr = one.get("ACTIVE").toString();
                idleStr = one.get("IDLE").toString();
                bscStr = one.get("BSC").toString();
                cellStr = one.get("CELL").toString();
                // 一行变两行
                // active
                if (vm.getMaxNum() < Integer.parseInt(activeStr) || vm.getMaxNum() > Integer.parseInt(activeStr)) {
                    baMap = new HashMap<String, Object>();
                    baMap.put("BSC", bscStr);
                    baMap.put("CELL", cellStr);
                    baMap.put("LISTTYPE", "ACTIVE");
                    baMap.put("NUM", activeStr);
                    result.add(baMap);
                }
                // idle
                if (vm.getMaxNum() < Integer.parseInt(idleStr) || vm.getMaxNum() > Integer.parseInt(idleStr)) {
                    baMap = new HashMap<String, Object>();
                    baMap.put("BSC", bscStr);
                    baMap.put("CELL", cellStr);
                    baMap.put("LISTTYPE", "IDLE");
                    baMap.put("NUM", idleStr);
                    result.add(baMap);
                }
            }
        }
        return result;
    }

    /**
     * 获取爱立信小区同频同bsic检查结果集
     */
    public List<Map<String, Object>> getEriCellCoBsicCheckFinalResult(GsmParamCheckVM vm) {
        double meaDis;
        if (vm.getCheckCoBsic() == false) {
            vm.setDistance(15000);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<CobsicCellsDTO> cobsicLists = saveCobsicCellsByCoBsicKeys(vm);
        Map<String, Object> cobsicMap = new HashMap<String, Object>();
        CobsicCellsDTO cobsicCells;
        List<String> cellLists;
        String bcch = "", bsic = "";
        if (cobsicLists != null && cobsicLists.size() != 0) {
            //最外范围循环 bcch,bsic,list<label>
            for (int k = 0; k < cobsicLists.size(); k++) {
                cellLists = cobsicLists.get(k).getCells();
                bcch = cobsicLists.get(k).getBcch() + "";
                bsic = cobsicLists.get(k).getBsic();
                String labels[] = new String[cellLists.size()];
                for (int i = 0; i < cellLists.size(); i++) {
                    labels[i] = cellLists.get(i).toString();
                }
                //某个bcch,bsic对下的label集合对象循环
                for (int i = 0; i < labels.length - 1; i++) {
                    // 循环比较两两间是否　距离小区15公里
                    for (int j = i + 1; j < labels.length; j++) {
                        // 判断－距离小区15公里 getDistanceBetweenTheCells
                        //改造：由sql计算距离改为由java通过经纬度计算距离
                        meaDis = getDistanceBetweenTheCells(labels[i], labels[j]);
                        if (meaDis != 0 && meaDis < vm.getDistance()) {
                            // (co-bsic距离小于15公里
                            if (cobsicMap.containsKey(bcch + "," + bsic)) {
                                //通过bcch,bsic为key从map中获取已存在的对象集合
                                cobsicCells = (CobsicCellsDTO) cobsicMap.get(bcch + "," + bsic);
                                //获取cobsic拓展的组合对象集合
                                List<CobsicCellsExpandDTO> cobsicexpanList = cobsicCells.getCombinedCells();
                                //新建cobsic拓展对象
                                CobsicCellsExpandDTO cellsExpand = new CobsicCellsExpandDTO();
                                cellsExpand.setCombinedCell(labels[i].substring(0, labels[i].lastIndexOf("-")) + "," + labels[j].substring(0, labels[j].lastIndexOf("-")));
                                cellsExpand.setMeaDis(meaDis);
                                cellsExpand.setMml("");
                                //为bcch,bsic的所在拓展的对象集合内新增对象
                                cobsicexpanList.add(cellsExpand);
                                cobsicCells.setCombinedCells(cobsicexpanList);
                                cobsicMap.put(bcch + "," + bsic, cobsicCells);
                            } else {
                                cobsicCells = new CobsicCellsDTO();
                                CobsicCellsExpandDTO cellsExpand = new CobsicCellsExpandDTO();
                                cellsExpand.setCombinedCell(labels[i].substring(0, labels[i].lastIndexOf("-")) + "," + labels[j].substring(0, labels[j].lastIndexOf("-")));
                                cellsExpand.setMeaDis(meaDis);
                                cellsExpand.setMml("");
                                cobsicCells.setBcch(Long.parseLong(bcch));
                                cobsicCells.setBsic(bsic);
                                List<CobsicCellsExpandDTO> list = new ArrayList<CobsicCellsExpandDTO>();
                                //向拓展cobsic集合中注入数据
                                list.add(cellsExpand);
                                //设置cobsic集合对象
                                cobsicCells.setCombinedCells(list);
                                //通过bcch,bsic为key向map中增加cobsic对象集合
                                cobsicMap.put(bcch + "," + bsic, cobsicCells);
                            }
                        }
                    }
                }
            }

        }
        //解析出页面所需数据
        for (Object o : cobsicMap.values()) {
            Map<String, Object> mapF = new HashMap<String, Object>();
            CobsicCellsDTO co = (CobsicCellsDTO) o;
            Long bcchF = co.getBcch();
            String bsicF = co.getBsic();
            String meaDisF = "",mml = "", bsc1 = "", cell1 = "", cell1Name = "", bsc2 = "", cell2 = "", cell2Name = "";
            List<CobsicCellsExpandDTO> combinedCells = co.getCombinedCells();
            for (CobsicCellsExpandDTO ce : combinedCells) {
                String combinedCell = ce.getCombinedCell();
                meaDisF = String.valueOf(ce.getMeaDis());
                mml = ce.getMml();
                String[] cc = combinedCell.split(",");
                bsc1 = cc[0].split("-")[0];
                cell1 = cc[0].split("-")[1];
                cell1Name = cc[0].split("-")[2];
                bsc2 = cc[1].split("-")[0];
                cell2 = cc[1].split("-")[1];
                cell2Name = cc[1].split("-")[2];
            }
            mapF.put("BSIC",bsicF);
            mapF.put("BCCH",bcchF);
            mapF.put("BSC1",bsc1);
            mapF.put("CELL1",cell1);
            mapF.put("CELL1_NAME",cell1Name);
            mapF.put("BSC2",bsc2);
            mapF.put("CELL2",cell2);
            mapF.put("CELL2_NAME",cell2Name);
            mapF.put("DISTANCE",Math.floor(Double.parseDouble(meaDisF)));
            mapF.put("MML",mml);
            result.add(mapF);
        }
        return result;
    }

    /**
     * 通过相同bcchbsic的组合cobsic下有两个或多个label,从而保存CobsicCells对象集合数据
     */
    public List<CobsicCellsDTO> saveCobsicCellsByCoBsicKeys(GsmParamCheckVM vm) {
        List<Map<String, Object>> res = gsmParamCheckMapper.getEriCellCoBsicCheckResult(vm);
        List<CobsicCellsDTO> coblists = new ArrayList<CobsicCellsDTO>();
        boolean flag = false;
        String listbcch = "", listbsic = "", label = "";
        for (int i = 0; i < res.size(); i++) {
            Map map = res.get(i);
            listbcch = map.get("BCCH").toString();
            listbsic = map.get("BSIC").toString();
            label = map.get("CELL").toString();
            List a = Arrays.asList(label);

            List arrayList = new ArrayList(a);
            CobsicCellsDTO onecobsicCell = new CobsicCellsDTO();
            onecobsicCell.setBcch(Long.parseLong(listbcch));
            onecobsicCell.setBsic(listbsic);
            onecobsicCell.setCells(arrayList);
            for (int j = 0; j < coblists.size(); j++) {
                boolean bcchbool = listbcch.equals(Long.toString(coblists.get(j)
                        .getBcch()));
                boolean bsicbool = listbsic.equals(coblists.get(j)
                        .getBsic());
                if (bcchbool && bsicbool) {
                    coblists.get(j).getCells().add(label);
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
            if (!flag) {
                coblists.add(onecobsicCell);
            }
        }
        return coblists;
    }

    public double getDistanceBetweenTheCells(String sourcecell, String targetcell) {
        double dis = 0;
        String lng1 = sourcecell.substring(sourcecell.lastIndexOf("-") + 1).split(",")[0];
        String lat1 = sourcecell.substring(sourcecell.lastIndexOf("-") + 1).split(",")[1];
        String lng2 = targetcell.substring(targetcell.lastIndexOf("-") + 1).split(",")[0];
        String lat2 = targetcell.substring(targetcell.lastIndexOf("-") + 1).split(",")[1];

        if (!"空".equals(lng1) && !"空".equals(lng2)) {
            dis = LatLngHelperUtils.Distance(Double.parseDouble(lng1), Double.parseDouble(lat1), Double.parseDouble(lng2), Double.parseDouble(lat2));
        }
        return dis;
    }

    /**
     * 获取爱立信小区Nccperm检查结果
     */
    private List<Map<String, Object>> getEriCellNccpermFinalResult(List<Map<String, Object>> res) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        String cell = "", nccpermStr = "", ncellNccStr = "", leakNcc = "", command = "", nccStr = "";

        String nccperm[] = {}, ncellNcc[] = {};

        List<String> nccpermList;

        //判断nccperm是否存在漏定，有则加入结果集
        for (Map<String, Object> one : res) {
            //初始化
            nccStr = "";
            leakNcc = "";
            if (one.get("NCCPERM") != null && one.get("NCELL_NCC") != null) {
                nccpermStr = one.get("NCCPERM").toString();
                ncellNccStr = one.get("NCELL_NCC").toString();
                if (nccpermStr.equals(ncellNccStr)) {
                    continue;
                } else {
                    nccperm = nccpermStr.split(",");
                    ncellNcc = ncellNccStr.split(",");
                    nccpermList = Arrays.asList(nccperm);
                    for (String ncc : nccperm) {
                        nccStr += ncc + "&";
                    }
                    for (String ncc : ncellNcc) {
                        if (!nccpermList.contains(ncc)) {
                            leakNcc += ncc + ",";
                            nccStr += ncc + "&";
                        }
                    }
                    if (("").equals(leakNcc)) {
                        //初始化，不存在漏定
                        continue;
                    }
                    leakNcc = leakNcc.substring(0, leakNcc.length() - 1);
                    nccStr = nccStr.substring(0, nccStr.length() - 1);
                    //加入缺失ncc元素
                    one.put("LEAK_NCC", leakNcc);
                    //加入指令元素
                    cell = one.get("CELL").toString();
                    command = "RLSSC:CELL=" + cell + ",NCCPERM=" + nccStr + ";";
                    one.put("COMMAND", command);
                    result.add(one);
                }
            }
        }
        return result;
    }

    /**
     * 获取爱立信小区测量频点多定义结果
     */
    private List<Map<String, Object>> getEriCellMeaFreqMultidefineResult(List<Map<String, Object>> res) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        String activeStr = "", idleStr = "", ncellBcchStr = "", actives[] = {}, idles[] = {}, ncellBcchs[] = {};

        List<String> ncellBcchList;

        String overActiveStr = "", overActiveComm = "", overIdleStr = "", overIdleComm = "", command = "";

        Map<String, Object> map = null;

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
                    if (!ncellBcchList.contains(active)) {
                        overActiveStr += active + ",";
                        overActiveComm += active + "&";
                    }
                }
                if (!("").equals(overActiveStr) && !("").equals(overActiveComm)) {
                    overActiveStr = overActiveStr.substring(0, overActiveStr.length() - 1);
                    overActiveComm = overActiveComm.substring(0, overActiveComm.length() - 1);
                    if (one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "ACTIVE");
                        map.put("OVER_DEFINE", overActiveStr);
                        command = "RLMFC:CELL=" + one.get("CELL").toString()
                                + ",MBCCHNO=" + overActiveComm + ",MRNIC,LISTTYPE=ACTIVE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
                //判断idle是否多定义
                for (String idle : idles) {
                    if (!ncellBcchList.contains(idle)) {
                        overIdleStr += idle + ",";
                        overIdleComm += idle + "&";
                    }
                }
                if (!("").equals(overIdleStr) && !("").equals(overIdleComm)) {
                    overIdleStr = overIdleStr.substring(0, overIdleStr.length() - 1);
                    overIdleComm = overIdleComm.substring(0, overIdleComm.length() - 1);
                    if (one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "IDLE");
                        map.put("OVER_DEFINE", overIdleStr);
                        command = "RLMFC:CELL=" + one.get("CELL").toString()
                                + ",MBCCHNO=" + overIdleComm + ",MRNIC,LISTTYPE=IDLE;";
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
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        String activeStr = "", idleStr = "", ncellBcchStr = "";

        String actives[] = {}, idles[] = {}, ncellBcchs[] = {};

        List<String> activeList;
        List<String> idleList;

        String leakActiveStr = "", leakActiveComm = "", leakIdleStr = "", leakIdleComm = "", command = "";

        Map<String, Object> map = null;

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
                idleList = Arrays.asList(idles);
                //判断active是否多定义
                for (String bcch : ncellBcchs) {
                    if (!activeList.contains(bcch)) {
                        leakActiveStr += bcch + ",";
                        leakActiveComm += bcch + "&";
                    }
                }
                if (!("").equals(leakActiveStr) && !("").equals(leakActiveComm)) {
                    leakActiveStr = leakActiveStr.substring(0, leakActiveStr.length() - 1);
                    leakActiveComm = leakActiveComm.substring(0, leakActiveComm.length() - 1);
                    if (one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "ACTIVE");
                        map.put("LEAK_DEFINE", leakActiveStr);
                        command = "RLMFC:CELL=" + one.get("CELL").toString()
                                + ",MBCCHNO=" + leakActiveComm + ",MRNIC,LISTTYPE=ACTIVE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
                //判断idle是否多定义
                for (String bcch : ncellBcchs) {
                    if (!idleList.contains(bcch)) {
                        leakIdleStr += bcch + ",";
                        leakIdleComm += bcch + "&";
                    }
                }
                if (!("").equals(leakIdleStr) && !("").equals(leakIdleComm)) {
                    leakIdleStr = leakIdleStr.substring(0, leakIdleStr.length() - 1);
                    leakIdleComm = leakIdleComm.substring(0, leakIdleComm.length() - 1);
                    if (one.get("BSC") != null && one.get("CELL") != null) {
                        map = new HashMap<String, Object>();
                        map.put("BSC", one.get("BSC"));
                        map.put("CELL", one.get("CELL"));
                        map.put("LISTTYPE", "IDLE");
                        map.put("LEAK_DEFINE", leakIdleStr);
                        command = "RLMFC:CELL=" + one.get("CELL").toString()
                                + ",MBCCHNO=" + leakIdleComm + ",MRNIC,LISTTYPE=IDLE;";
                        map.put("COMMAND", command);
                        result.add(map);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取爱立信小区同邻频检查结果
     */
    private List<Map<String, Object>> getEriCellSameNcellFreqCheckResult(List<Map<String, Object>> res) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        ;

        String comment = "", bsc = "", cell = "", ncell = "", bcch = "", tch = "", nbcch = "", ntch = "", cs = "";

        String tchStr[] = {}, ntchStr[] = {};

        List<String> tchList;
        List<String> ntchList;

        Map<String, Object> map;

        String distance = "", bcchR = "", bcchL = "", nbcchR = "", nbcchL = "", tchR = "", tchL = "";

        for (Map<String, Object> one : res) {
            //初始化
            comment = "";

            if (one.get("CELL") == null || one.get("N_CELL") == null
                    || one.get("BCCHNO") == null || one.get("N_BCCHNO") == null
                    || one.get("TCH") == null || one.get("N_TCH") == null
                    || one.get("DISTANCE") == null) {
                continue;
            }

            bsc = one.get("BSC").toString();
            cell = one.get("CELL").toString();
            ncell = one.get("N_CELL").toString();
            bcch = one.get("BCCHNO").toString();
            nbcch = one.get("N_BCCHNO").toString();
            tch = one.get("TCH").toString();
            ntch = one.get("N_TCH").toString();
            cs = one.get("CS").toString();
            distance = one.get("DISTANCE").toString();

            if (("YES").equals(cs)) {
                comment = "同站";
            } else if (("NO").equals(cs)) {
                comment = "邻站";
            }

            tchStr = tch.split(",");
            ntchStr = ntch.split(",");
            tchList = Arrays.asList(tchStr);
            ntchList = Arrays.asList(ntchStr);

            //同BCCH
            if (ntchList.contains(bcch)) {
                map = new HashMap<String, Object>();
                map.put("BSC", bsc);
                map.put("CELL", cell);
                map.put("CELLR", ncell);
                map.put("CELL_BCCH", bcch);
                map.put("CELLR_BCCH", nbcch);
                map.put("CELL_FREQ", bcch);
                map.put("CELLR_FREQ", bcch);
                map.put("DIR", "MUTAUL");
                map.put("CS", cs);
                map.put("DISTANCE", distance);
                map.put("COMMENT", comment + "同BCCH");
                result.add(map);
            }

            if (tchList.contains(nbcch)) {
                map = new HashMap<String, Object>();
                map.put("BSC", bsc);
                map.put("CELL", cell);
                map.put("CELLR", ncell);
                map.put("CELL_BCCH", bcch);
                map.put("CELLR_BCCH", nbcch);
                map.put("CELL_FREQ", nbcch);
                map.put("CELLR_FREQ", nbcch);
                map.put("DIR", "MUTAUL");
                map.put("CS", cs);
                map.put("DISTANCE", distance);
                map.put("COMMENT", comment + "同BCCH");
                result.add(map);
            }
            //同TCH
            for (String t : tchList) {
                //过滤tch中的bcch，以免重复
                if (t.equals(bcch)) {
                    continue;
                }
                if (ntchList.contains(t)) {
                    //过滤ntch中的nbcch，以免重复
                    if (t.equals(nbcch)) {
                        continue;
                    }
                    map = new HashMap<String, Object>();
                    map.put("BSC", bsc);
                    map.put("CELL", cell);
                    map.put("CELLR", ncell);
                    map.put("CELL_BCCH", bcch);
                    map.put("CELLR_BCCH", nbcch);
                    map.put("CELL_FREQ", t);
                    map.put("CELLR_FREQ", t);
                    map.put("DIR", "MUTAUL");
                    map.put("CS", cs);
                    map.put("DISTANCE", distance);
                    map.put("COMMENT", comment + "同TCH");
                    result.add(map);
                }
            }
            //邻BCCH
            bcchR = (Integer.parseInt(bcch) + 1) + ""; //加1
            bcchL = (Integer.parseInt(bcch) - 1) + ""; //减1
            nbcchR = (Integer.parseInt(bcch) + 1) + "";
            nbcchL = (Integer.parseInt(bcch) - 1) + "";
            if (ntchList.contains(bcchR)) {
                map = new HashMap<String, Object>();
                map.put("BSC", bsc);
                map.put("CELL", cell);
                map.put("CELLR", ncell);
                map.put("CELL_BCCH", bcch);
                map.put("CELLR_BCCH", nbcch);
                map.put("CELL_FREQ", bcch);
                map.put("CELLR_FREQ", bcchR);
                map.put("DIR", "MUTAUL");
                map.put("CS", cs);
                map.put("DISTANCE", distance);
                map.put("COMMENT", comment + "邻BCCH");
                result.add(map);
            }
            if (ntchList.contains(bcchL)) {
                map = new HashMap<String, Object>();
                map.put("BSC", bsc);
                map.put("CELL", cell);
                map.put("CELLR", ncell);
                map.put("CELL_BCCH", bcch);
                map.put("CELLR_BCCH", nbcch);
                map.put("CELL_FREQ", bcch);
                map.put("CELLR_FREQ", bcchL);
                map.put("DIR", "MUTAUL");
                map.put("CS", cs);
                map.put("DISTANCE", distance);
                map.put("COMMENT", comment + "邻BCCH");
                result.add(map);
            }
            if (tchList.contains(nbcchR)) {
                map = new HashMap<String, Object>();
                map.put("BSC", bsc);
                map.put("CELL", cell);
                map.put("CELLR", ncell);
                map.put("CELL_BCCH", bcch);
                map.put("CELLR_BCCH", nbcch);
                map.put("CELL_FREQ", nbcchR);
                map.put("CELLR_FREQ", nbcch);
                map.put("DIR", "MUTAUL");
                map.put("CS", cs);
                map.put("DISTANCE", distance);
                map.put("COMMENT", comment + "邻BCCH");
                result.add(map);
            }
            if (tchList.contains(nbcchL)) {
                map = new HashMap<String, Object>();
                map.put("BSC", bsc);
                map.put("CELL", cell);
                map.put("CELLR", ncell);
                map.put("CELL_BCCH", bcch);
                map.put("CELLR_BCCH", nbcch);
                map.put("CELL_FREQ", nbcchL);
                map.put("CELLR_FREQ", nbcch);
                map.put("DIR", "MUTAUL");
                map.put("CS", cs);
                map.put("DISTANCE", distance);
                map.put("COMMENT", comment + "邻BCCH");
                result.add(map);
            }
            //邻TCH
            for (String t : tchList) {
                //过滤tch中的bcch，以免重复
                if (t.equals(bcch)) {
                    continue;
                }
                tchR = (Integer.parseInt(t) + 1) + "";
                tchL = (Integer.parseInt(t) - 1) + "";
                //过滤tch中的邻nbcch，以免重复
                if (tchR.equals(nbcch)) {
                    continue;
                }
                if (ntchList.contains(tchR)) {
                    map = new HashMap<String, Object>();
                    map.put("BSC", bsc);
                    map.put("CELL", cell);
                    map.put("CELLR", ncell);
                    map.put("CELL_BCCH", bcch);
                    map.put("CELLR_BCCH", nbcch);
                    map.put("CELL_FREQ", t);
                    map.put("CELLR_FREQ", tchR);
                    map.put("DIR", "MUTAUL");
                    map.put("CS", cs);
                    map.put("DISTANCE", distance);
                    map.put("COMMENT", comment + "邻TCH");
                    result.add(map);
                }
                //过滤tch中的邻nbcch，以免重复
                if (tchL.equals(nbcch)) {
                    continue;
                }
                if (ntchList.contains(tchL)) {
                    map = new HashMap<String, Object>();
                    map.put("BSC", bsc);
                    map.put("CELL", cell);
                    map.put("CELLR", ncell);
                    map.put("CELL_BCCH", bcch);
                    map.put("CELLR_BCCH", nbcch);
                    map.put("CELL_FREQ", t);
                    map.put("CELLR_FREQ", tchL);
                    map.put("DIR", "MUTAUL");
                    map.put("CS", cs);
                    map.put("DISTANCE", distance);
                    map.put("COMMENT", comment + "邻TCH");
                    result.add(map);
                }
            }
        }
        return result;
    }
}
