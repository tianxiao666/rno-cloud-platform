package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.domain.indoor.CbBuilding;
import com.hgicreate.rno.service.indoor.CbBuildingService;
import com.hgicreate.rno.service.indoor.dto.CbBuildingDTO;
import com.hgicreate.rno.web.rest.indoor.vm.CbBuildingDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/cb-buliding-data")
public class CbBuildingDataResource {

    private final CbBuildingService cbBuildingService;

    public CbBuildingDataResource(CbBuildingService cbBuildingService) {
        this.cbBuildingService = cbBuildingService;
    }

    @PostMapping("/cb-building-query")
    public List<CbBuildingDTO> cbBuildingQuery(CbBuildingDataQueryVM vm){
        log.debug("进入视图资源cbBuildingQuery 查询建筑场所数据,vm={}",vm);
        return cbBuildingService.queryCbBuildingDTOs(vm);
    }

    @GetMapping("/cb-building-edit")
    public CbBuilding cbBuildingEdit(CbBuildingDataQueryVM vm){
        log.debug("进入视图资源cbBuildingEdit 编辑建筑场所数据,vm={}",vm);
        return cbBuildingService.editCbBuilding(vm);
    }

    @PostMapping("/cb-building-status-update")
    public int cbBuildingStatusUpdate(CbBuildingDataQueryVM vm){
        log.debug("进入视图资源cbBuildingStatusUpdate 更新建筑场所状态数据,vm={}",vm);
        return cbBuildingService.updateCbBuildingStatus(vm);
    }

    @PostMapping("/cb-building-save")
    public void cbBuildingSave(CbBuilding cbBuilding){
        log.debug("进入视图资源cbBuildingUpdate 保存建筑场所数据,cbBuilding={}",cbBuilding);
        cbBuildingService.saveCbBuilding(cbBuilding);
    }

    @PostMapping("/cb-building-update")
    public int cbBuildingUpdate(CbBuilding cbBuilding){
        log.debug("进入视图资源cbBuildingUpdate 编辑更新建筑场所数据,cbBuilding={}",cbBuilding);
       return cbBuildingService.cbBuildingUpdate(cbBuilding);
    }

    @DeleteMapping("/cb-building-delete")
    public void cbBuildingDelete(CbBuildingDataQueryVM vm){
        log.debug("进入视图资源cbBuildingUpdate 删除建筑场所数据,CbBuildingDataQueryVM={}",vm);
        cbBuildingService.deleteCbBuildingByBuildingId(vm);
    }

    @PostMapping("/cb-building-query-all")
    public List<CbBuilding> cbBuildingQueryAll(){
        log.debug("进入视图资源cbBuildingQueryAll 全部建筑场所数据");
        return cbBuildingService.findAll();
    }
}
