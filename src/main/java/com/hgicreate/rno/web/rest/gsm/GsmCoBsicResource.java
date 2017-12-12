package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import com.hgicreate.rno.mapper.gsm.GsmCoBsicMapper;
import com.hgicreate.rno.service.gsm.GsmCoBsicService;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsDTO;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsExpandDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/gsm-co-bsic-analysis")
public class GsmCoBsicResource {

    private final GsmCoBsicMapper gsmCoBsicMapper;

    private final GsmCoBsicService gsmCoBsicService;

    public GsmCoBsicResource(GsmCoBsicMapper gsmCoBsicMapper, GsmCoBsicService gsmCoBsicService) {
        this.gsmCoBsicMapper = gsmCoBsicMapper;
        this.gsmCoBsicService = gsmCoBsicService;
    }

    @GetMapping("/whole-net-cobsic-query")
    public Map<String, Object> getWholeNetCoBsicCells(GsmCoBsicQueryVM vm){
        List<CobsicCellsDTO> cobsicCells = gsmCoBsicService.getCobsicCells(vm);
        return getCoBsic(cobsicCells);
    }

    private void prepareCellsExpand(List<GsmNcellRelation> list,CobsicCellsExpandDTO gsmCobsicCellsExpand){
        for (int l = 0; l < list.size(); l++) {
            log.info("获取共同邻区共多少：" + list.size());
            gsmCobsicCellsExpand.setWhetherComNcell(true);
            gsmCobsicCellsExpand.setCommonNcell(list.get(l).getCellId());
        }
    }

    @GetMapping("/cobsic-query-by-bcch-bsic")
    public Map<String, Object> getCoBsicCellsByBcchAndBsic(GsmCoBsicQueryVM vm){
        List<CobsicCellsDTO> cobsicCells = gsmCoBsicService.getCobsicCellsByBcchAndBsic(vm);
        return getCoBsic(cobsicCells);
    }

    private Map<String, Object> getCoBsic(List<CobsicCellsDTO> cobsicCells){
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> cobsicMap = new HashMap<>();

        List<String> cellStrs;
        CobsicCellsDTO gsmCobsicCells;
        String bcch;
        String bsic;
        if(cobsicCells != null && cobsicCells.size() != 0){
            for(CobsicCellsDTO one : cobsicCells){
                cellStrs = one.getCells();
                bcch = one.getBcch() + "";
                bsic = one.getBsic() + "";
                //集合转成数组
                String [] labels = cellStrs.toArray(new String [cellStrs.size()]);
                for(int i = 0 ; i < labels.length - 1; i++){
                    //循环判断小区两两之间是否为邻区，或为某小区共同邻区 且距离小于2000米
                    for(int j = i+1; j< labels.length; j++){
                        boolean isNcell = gsmCoBsicMapper.queryNcell(labels[i], labels[j]) != null
                                && gsmCoBsicMapper.queryNcell(labels[i], labels[j]).size() != 0;
                        List<GsmNcellRelation> ncells= gsmCoBsicMapper.queryCommonNcellByTwoCell(labels[i],labels[j]);
                        double distance = gsmCoBsicService.getDistanceBetweenCells(labels[i],labels[j]);
                        if((isNcell|| (ncells !=null && ncells.size() !=0))&& distance < 2000){
                            log.debug("isNcell:" + isNcell);
                            if (cobsicMap.containsKey(bcch + "," + bsic)) {
                                // 通过bcch,bsic为key从map中获取已存在的对象集合
                                gsmCobsicCells = (CobsicCellsDTO) cobsicMap.get(bcch + "," + bsic);
                                // 获取cobsic拓展的组合对象集合
                                List<CobsicCellsExpandDTO> cobsicexpanList =gsmCobsicCells.getCombinedCells();
                                log.info("cobsicCells.getCells():" + cobsicexpanList);
                                // 新建cobsic拓展对象
                                CobsicCellsExpandDTO cellsExpand = new CobsicCellsExpandDTO();
                                cellsExpand.setCombinedCell(labels[i] + "," + labels[j]);
                                cellsExpand.setWhetherNcell(isNcell);
                                cellsExpand.setWhetherComNcell(false);
                                prepareCellsExpand(ncells,cellsExpand);
                                // 为bcch,bsic的所在拓展的对象集合内新增对象
                                cobsicexpanList.add(cellsExpand);
                                gsmCobsicCells.setCombinedCells(cobsicexpanList);
                                cobsicMap.put(bcch + "," + bsic, gsmCobsicCells);
                            } else {
                                gsmCobsicCells = new CobsicCellsDTO();
                                CobsicCellsExpandDTO cellsExpand = new CobsicCellsExpandDTO();
                                cellsExpand.setCombinedCell(labels[i] + "," + labels[j]);
                                cellsExpand.setWhetherNcell(isNcell);
                                prepareCellsExpand(ncells,cellsExpand);
                                gsmCobsicCells.setBcch(Long.parseLong(bcch));
                                gsmCobsicCells.setBsic(bsic);
                                List<CobsicCellsExpandDTO> list = new ArrayList<>();
                                // 向拓展cobsic集合中注入数据
                                list.add(cellsExpand);
                                // 设置cobsic集合对象
                                gsmCobsicCells.setCombinedCells(list);
                                // 通过bcch,bsic为key向map中增加cobsic对象集合
                                cobsicMap.put(bcch + "," + bsic, gsmCobsicCells);
                            }

                        }

                    }
                }
            }
            if( !cobsicMap.isEmpty()){
                return cobsicMap;
            }
        }else{
            result.put("fail","不存在co-bsic小区！");
            return result;
        }
        result.put("fail","不存在co-bsic小区！");
        return result;
    }
}
