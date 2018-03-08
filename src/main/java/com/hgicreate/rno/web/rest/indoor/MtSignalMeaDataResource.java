package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.service.indoor.MtSignalMeaDataService;
import com.hgicreate.rno.service.indoor.dto.MtSignalMeaDataInfoDTO;
import com.hgicreate.rno.web.rest.indoor.vm.MtSignalMeaDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/mt-signal-mea-data")
public class MtSignalMeaDataResource {

    private final MtSignalMeaDataService mtSignalMeaDataService;

    public MtSignalMeaDataResource(MtSignalMeaDataService mtSignalMeaDataService) {
        this.mtSignalMeaDataService = mtSignalMeaDataService;
    }

    @PostMapping("/mt-signal-query")
    public List<MtSignalMeaDataInfoDTO> mtSignalQuery(MtSignalMeaDataQueryVM vm) {
        log.debug("进入视图资源mtSignalQuery 查询移动终端信号测量数据,city={},startDate={},endDate={},vm={}",
                vm.getCity(), vm.getBeginDate(), vm.getEndDate(), vm);
        return mtSignalMeaDataService.queryMtSignalMeaDataDTOs(vm);
    }

    @GetMapping("/mt-signal-collection")
    public void mtSignalCollection(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口mtSignalCollection MT测量数据采集,jsonArrayObj={}",
                jsonArrayObj);
        boolean flag = mtSignalMeaDataService.saveMtSignalMeaData(jsonArrayObj);
        if (flag){
            log.debug("退出资源 mtSignalCollection： MT测量数据采集保存成功！");
        }else {
            log.debug("退出资源 mtSignalCollection： MT测量数据采集保存失败！");
        }
    }
}
