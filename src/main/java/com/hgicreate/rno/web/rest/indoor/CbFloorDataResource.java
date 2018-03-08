package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.domain.indoor.CbFloor;
import com.hgicreate.rno.service.indoor.CbFloorService;
import com.hgicreate.rno.service.indoor.dto.CbFloorDTO;
import com.hgicreate.rno.web.rest.indoor.vm.CbFloorQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/cb-floor-data")
public class CbFloorDataResource {

    private final CbFloorService cbFloorService;

    public CbFloorDataResource(CbFloorService cbFloorService) {
        this.cbFloorService = cbFloorService;
    }

    @PostMapping("/cb-floor-query")
    public List<CbFloorDTO> cbFloorQuery(CbFloorQueryVM vm){
        log.debug("进入视图资源cbFloorQuery 查询楼层数据,vm={}",vm);
        return cbFloorService.cbFloorQuery(vm);
    }

    @PostMapping("/cb-floor-status-update")
    public int cbFloorStatusUpdate(CbFloorQueryVM vm){
        log.debug("进入视图资源cbFloorStatusUpdate 更新楼层状态数据,vm={}",vm);
        return cbFloorService.updateFloorStatus(vm);
    }

    @DeleteMapping("/cb-floor-delete")
    public void cbFloorDelete(CbFloorQueryVM vm){
        log.debug("进入视图资源cbFloorDelete 删除楼层数据,CbFloorQueryVM={}",vm);
        cbFloorService.deleteCbFloorByFloorId(vm);
    }

    @PostMapping("/cb-floor-save")
    public void cbFloorSave(CbFloor cbFloor){
        log.debug("进入视图资源cbFloorSave 保存楼层数据,cbFloor={}",cbFloor);
        cbFloorService.saveCbFloor(cbFloor);
    }

    @PostMapping("/cb-floor-edit")
    public CbFloor cbFloorEdit(CbFloorQueryVM vm){
        log.debug("进入视图资源cbFloorEdit 编辑楼层数据,vm={}",vm);
        return cbFloorService.editCbFloor(vm);
    }

    @PostMapping("/cb-floor-update")
    public int cbFloorUpdate(CbFloor cbFloor){
        log.debug("进入视图资源cbFloorUpdate 编辑更新楼层数据,cbFloor={}",cbFloor);
        return cbFloorService.cbFloorUpdate(cbFloor);
    }

    @PostMapping("/cb-floor-query-by-building")
    public List<CbFloor> cbFloorsQueryByBuilding(CbFloorQueryVM vm){
        log.debug("进入视图资源cbFloorsQueryByBuilding 某建筑场所下所有楼层数据");
        return cbFloorService.findByBuildingId(vm);
    }
}
