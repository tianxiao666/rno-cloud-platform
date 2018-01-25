package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.service.indoor.IdealApMeaDataService;
import com.hgicreate.rno.service.indoor.dto.IdealApMeaDataInfoDTO;
import com.hgicreate.rno.web.rest.indoor.vm.IdealApMeaDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ideal-ap-mea-data")
public class IdealApMeaDataResource {

    private final IdealApMeaDataService idealApMeaDataService;

    public IdealApMeaDataResource(IdealApMeaDataService idealApMeaDataService) {
        this.idealApMeaDataService = idealApMeaDataService;
    }

    @PostMapping("/idea-ap-query")
    public List<IdealApMeaDataInfoDTO> ideaApQuery(IdealApMeaDataQueryVM vm) {
        log.debug("进入视图资源ideaApQuery 查询理想AP测量数据,city={},startDate={},endDate={},vm={}",
                vm.getCity(), vm.getBeginDate(), vm.getEndDate(), vm);
        return idealApMeaDataService.queryIdealApMeaDataDTOs(vm);
    }

    @GetMapping("/ideal-ap-collection")
    public void idealApCollection(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口IdealApCollection 理想AP测量数据采集,jsonArrayObj={}",
                jsonArrayObj);
        boolean flag = idealApMeaDataService.saveIdealApMeaData(jsonArrayObj);
        if (flag){
            log.debug("退出资源 idealApCollection： 理想AP测量数据采集保存成功！");
        }else {
            log.debug("退出资源 idealApCollection： 理想AP测量数据采集保存失败！");
        }
    }
}
