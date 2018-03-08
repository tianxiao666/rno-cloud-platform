package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.domain.indoor.ApEquipment;
import com.hgicreate.rno.service.indoor.ApEquipmentService;
import com.hgicreate.rno.service.indoor.dto.ApEquipmentDTO;
import com.hgicreate.rno.web.rest.indoor.vm.ApEquipmentDataQueryVM;
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
@RequestMapping("/api/ap-equipment-data")
public class ApEquipmentResource {

    private final ApEquipmentService apEquipmentService;

    public ApEquipmentResource(ApEquipmentService apEquipmentService) {
        this.apEquipmentService = apEquipmentService;
    }

    @PostMapping("/ap-equipment-query")
    public List<ApEquipmentDTO> apEquipmentQuery(ApEquipmentDataQueryVM vm){
        log.debug("进入视图资源apEquipmentQuery 查询定位设备数据,vm={}",vm);
        return apEquipmentService.apEquipmentQuery(vm);
    }

    @PostMapping("/ap-equipment-status-update")
    public int apEquipmentStatusUpdate(ApEquipmentDataQueryVM vm){
        log.debug("进入视图资源apEquipmentStatusUpdate 更新定位设备状态数据,vm={}",vm);
        return apEquipmentService.updateApEquipmentStatus(vm);
    }

    @PostMapping("/ap-equipment-save")
    public void apEquipmentSave(ApEquipment apEquipment){
        log.debug("进入视图资源apEquipmentSave 保存AP定位设备数据,apEquipment={}",apEquipment);
        apEquipmentService.saveApEquipment(apEquipment);
    }

    @PostMapping("/ap-equipment-edit")
    public ApEquipment apEquipmentEdit(ApEquipmentDataQueryVM vm){
        log.debug("进入视图资源apEquipmentEdit 编辑ap定位信息数据,vm={}",vm);
        return apEquipmentService.editApEquipment(vm);
    }

    @DeleteMapping("/ap-equipment-delete")
    public void apEquipmentDelete(ApEquipmentDataQueryVM vm){
        log.debug("进入视图资源apEquipmentDelete 删除AP定位信息数据,ApEquipmentDataQueryVM={}",vm);
        apEquipmentService.deleteApEquipmentByApId(vm);
    }
}
