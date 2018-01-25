package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.domain.indoor.CbPoi;
import com.hgicreate.rno.service.indoor.CbPoiService;
import com.hgicreate.rno.service.indoor.dto.CbPoiDTO;
import com.hgicreate.rno.web.rest.indoor.vm.CbPoiDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cb-poi-data")
public class CbPoiResource {

    private final CbPoiService cbPoiService;

    public CbPoiResource(CbPoiService cbPoiService) {
        this.cbPoiService = cbPoiService;
    }

    @PostMapping("/cb-poi-query")
    public List<CbPoiDTO> cbPoiQuery(CbPoiDataQueryVM vm){
        log.debug("进入视图资源cbPoiQuery 查询兴趣点数据,vm={}",vm);
        return cbPoiService.cbPoiQuery(vm);
    }

    @PostMapping("/cb-poi-status-update")
    public int cbPoiStatusUpdate(CbPoiDataQueryVM vm){
        log.debug("进入视图资源cbPoiStatusUpdate 更新兴趣点状态数据,vm={}",vm);
        return cbPoiService.updateCbPoiStatus(vm);
    }

    @PostMapping("/cb-poi-save")
    public void cbPoiSave(CbPoi cbPoi){
        log.debug("进入视图资源cbPoiSave 保存兴趣点数据,cbPoi={}",cbPoi);
        cbPoiService.saveCbPoi(cbPoi);
    }

    @PostMapping("/cb-poi-edit")
    public CbPoi cbPoiEdit(CbPoiDataQueryVM vm){
        log.debug("进入视图资源cbPoiEdit 编辑poi兴趣点信息数据,vm={}",vm);
        return cbPoiService.editCbPoi(vm);
    }

    @DeleteMapping("/cb-poi-delete")
    public void cbPoiDelete(CbPoiDataQueryVM vm){
        log.debug("进入视图资源cbPoiDelete 删除POI兴趣点信息数据,CbPoiDataQueryVM={}",vm);
        cbPoiService.deleteCbPoiByPoiId(vm);
    }
}
