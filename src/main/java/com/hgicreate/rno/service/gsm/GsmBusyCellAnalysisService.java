package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import com.hgicreate.rno.mapper.gsm.GsmTrafficMapper;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.repository.gsm.GsmNcellRelationRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GsmBusyCellAnalysisService {
    private final GsmTrafficMapper gsmTrafficMapper;
    private final GsmCellDataRepository gsmCellDataRepository;

    private final GsmNcellRelationRepository gsmNcellRelationRepository;

    public GsmBusyCellAnalysisService(GsmTrafficMapper gsmTrafficMapper, GsmCellDataRepository gsmCellDataRepository,
                                      GsmNcellRelationRepository gsmNcellRelationRepository) {
        this.gsmTrafficMapper = gsmTrafficMapper;
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.gsmNcellRelationRepository = gsmNcellRelationRepository;
    }

    public List<Map<String, Object>> getBusyCell(Long areaId) {
        List<Map<String, Object>> cellsMap = gsmTrafficMapper.busyCellQuery(areaId);
        List<Map<String, Object>> result = new ArrayList<>();
        if (cellsMap.size() > 0) {
            for (Map<String, Object> cell : cellsMap) {
                GsmCell gsmCell = new GsmCell();
                gsmCell.setEnName(cell.get("CELL").toString());
                Example<GsmCell> example = Example.of(gsmCell);
                Map<String, Object> map = new HashMap<>();
                GsmCell cellResult = gsmCellDataRepository.findOne(example);
                if(cellResult!=null){
                    map.put("cellId", cellResult.getCellId());
                    map.put("cellName", cellResult.getCellName());
                    result.add(map);
                }
            }
        }
        return result;
    }

    public List<Map<String,Object>> getIdleNcell(String cellId, Long areaId) {
        List<Map<String,Object>> idleNcellList = new ArrayList<>();
        List<GsmNcellRelation> ncells = gsmNcellRelationRepository.findByCellId(cellId);
        List<Map<String, Object>> idleCells = gsmTrafficMapper.idleCellQuery(areaId);
        if (ncells.size() > 0 && idleCells.size() > 0) {
            for (GsmNcellRelation gsmNcell : ncells) {
                Map<String,Object> map = new HashMap<>();
                for (Map<String, Object> cell : idleCells) {
                    GsmCell gsmCell = new GsmCell();
                    gsmCell.setEnName(cell.get("CELL").toString());
                    Example<GsmCell> example = Example.of(gsmCell);
                    GsmCell cellResult = gsmCellDataRepository.findOne(example);
                    if (gsmNcell.getNcellId().equals(cellResult.getCellId())) {
                        map.put("cellId",gsmNcell.getNcellId());
                        map.put("cellName",cellResult.getCellName());
                        idleNcellList.add(map);
                        break;
                    }
                }
            }
        }
        return idleNcellList;
    }
}
