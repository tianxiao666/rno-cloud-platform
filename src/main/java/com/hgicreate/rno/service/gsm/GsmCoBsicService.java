package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmCoBsicMapper;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsDTO;
import com.hgicreate.rno.util.LatLngHelperUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GsmCoBsicService {

    private final GsmCoBsicMapper gsmCoBsicMapper;

    public GsmCoBsicService(GsmCoBsicMapper gsmCoBsicMapper) {
        this.gsmCoBsicMapper = gsmCoBsicMapper;
    }

    public List<CobsicCellsDTO> getCobsicCells(GsmCoBsicQueryVM vm){
        log.debug("进入查询cobsic干扰方法areaIds={}", vm.getAreaId() );
        List<Map<String,Object>> list = gsmCoBsicMapper.getCoBsicCellsByAreaId(vm);
        return getCoBsicCellsDTO(list);
    }

    public double getDistanceBetweenCells(String sourceCell, String targetCell){
        List<String> lonlats = gsmCoBsicMapper.getLonLatsByCells(sourceCell,targetCell);
        String lonlat[] = lonlats.get(0).split(",");
        return LatLngHelperUtils.Distance(Double.parseDouble(lonlat[0]),Double.parseDouble(lonlat[1]),
                Double.parseDouble(lonlat[2]),Double.parseDouble(lonlat[3]));
    }

    public List<CobsicCellsDTO> getCobsicCellsByBcchAndBsic(GsmCoBsicQueryVM vm){
        log.debug("进入查询cobsic干扰方法areaIds={}", vm.getAreaId() );

        List<Map<String,Object>> list = gsmCoBsicMapper.getCoBsicCellsByAreaIdAndBcch(vm);
       return getCoBsicCellsDTO(list);
    }

    private List<CobsicCellsDTO> getCoBsicCellsDTO(List<Map<String,Object>> list){
        List<CobsicCellsDTO> cobsicCells = new ArrayList<>();
        log.debug("list大小为={}",list.size());
        boolean flag = false;
        for(Map<String,Object> map : list){
            String bcch = map.get("bcch").toString();
            String bsic = map.get("bsic").toString();
            String label = map.get("cellId").toString();
            CobsicCellsDTO oneCell = new CobsicCellsDTO();
            oneCell.setBcch(Long.parseLong(bcch));
            oneCell.setBsic(bsic);
            List<String> cellList = new ArrayList<>();
            cellList.add(label);
            oneCell.setCells(cellList);
            for (CobsicCellsDTO cobsicCell : cobsicCells) {
                boolean isBcchEqual = bcch.equals(Long.toString(cobsicCell.getBcch()));
                boolean isBsicEqual = bsic.equals(cobsicCell.getBsic());
                if (isBcchEqual && isBsicEqual) {
                    cobsicCell.getCells().add(label);
                    flag = true;
                } else {
                    flag = false;
                }
            }
            if(!flag){
                cobsicCells.add(oneCell);
            }
        }
        return cobsicCells;
    }

}
