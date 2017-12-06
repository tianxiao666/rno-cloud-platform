package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.repository.gsm.BscDataRepository;
import com.hgicreate.rno.service.gsm.BscDataService;
import com.hgicreate.rno.service.gsm.dto.BscDataDTO;
import com.hgicreate.rno.service.gsm.mapper.BscDataMessageMapper;
import com.hgicreate.rno.web.rest.gsm.vm.ParamCheckQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/gsm-param-query")
public class ParamQueryResource {

    private final BscDataService bscDataService;
    private final BscDataRepository bscDataRepository;

    public ParamQueryResource(BscDataService bscDataService,
                              BscDataRepository bscDataRepository) {
        this.bscDataService = bscDataService;
        this.bscDataRepository = bscDataRepository;
    }

    @GetMapping("/query-param")
    public /*List<BscReportDTO>*/ void queryParam(ParamCheckQueryVM vm) throws ParseException {
        log.debug("进入一致性检查方法。");
        log.debug("视图模型: " + vm);
        // return bscDataService.queryTrafficData(vm);
    }

    @GetMapping("/query-bsc-by-cityId")
    public List<BscDataDTO> queryReport(String cityId){
        log.debug("查询bsc的区域id为：{}",cityId);
        Area area = new Area();
        area.setId(Long.parseLong(cityId));
        return bscDataRepository.findByAreaAndStatus(area,"N")
                .stream().map(BscDataMessageMapper.INSTANCE::bscDataToBscDataDto)
                .collect(Collectors.toList());
    }


}
