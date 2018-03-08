package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.service.indoor.DmLayerElementService;
import com.hgicreate.rno.service.indoor.dto.DmLayerElementDTO;
import com.hgicreate.rno.web.rest.indoor.vm.DmLayerElementDataQueryVM;
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
@RequestMapping("/api/dm-layer-element-data")
public class DmLayerElementResource {

    private final DmLayerElementService dmLayerElementService;

    public DmLayerElementResource(DmLayerElementService dmLayerElementService) {
        this.dmLayerElementService = dmLayerElementService;
    }

    @PostMapping("/dm-layer-element-query-by-layerid")
    public List<DmLayerElementDTO> dmLayerElementQuery(DmLayerElementDataQueryVM vm){
        log.debug("进入视图资源dmLayerElementQuery 查询图层元素数据,vm={}",vm);
        return dmLayerElementService.dmLayerElementQuery(vm);
    }
}
