package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.service.indoor.DmDrawMapService;
import com.hgicreate.rno.service.indoor.dto.DmDrawMapDTO;
import com.hgicreate.rno.web.rest.indoor.vm.DmDrawMapDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/dm-draw-map-data")
public class DmDrawMapResource {

    private final DmDrawMapService dmDrawMapService;

    public DmDrawMapResource(DmDrawMapService dmDrawMapService) {
        this.dmDrawMapService = dmDrawMapService;
    }

    @PostMapping("/dm-draw-map-query")
    public List<DmDrawMapDTO> dmDrawMapQuery(DmDrawMapDataQueryVM vm){
        log.debug("进入视图资源dmDrawMapQuery 查询楼层平面图数据,vm={}",vm);
        return dmDrawMapService.dmDrawMapQuery(vm);
    }

    @PostMapping("/floor-planegraph-status-update")
    public int cbFloorPlanegraphStatusUpdate(DmDrawMapDataQueryVM vm){
        log.debug("进入视图资源cbFloorPlanegraphStatusUpdate 更新楼层平面图状态数据,vm={}",vm);
        return dmDrawMapService.updateFloorPlanegraphStatus(vm);
    }

    @PostMapping("/dm-drawmap-query-by-floorid")
    public List<DmDrawMapDTO> dmDrawMapQueryByFloorId(DmDrawMapDataQueryVM vm){
        log.debug("进入视图资源dmDrawMapQueryByFloorId 通过floorId查询楼层平面图数据,vm={}",vm);
        return dmDrawMapService.dmDrawMapQueryByFloorId(vm);
    }
}
