package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.repository.gsm.BscDataRepository;
import com.hgicreate.rno.service.gsm.ParamCheckService;
import com.hgicreate.rno.service.gsm.dto.BscDataDTO;
import com.hgicreate.rno.service.gsm.mapper.BscDataMessageMapper;
import com.hgicreate.rno.web.rest.gsm.vm.ParamCheckVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/gsm-param-check")
public class ParamCheckResource {

    private final ParamCheckService paramCheckService;
    private final BscDataRepository bscDataRepository;

    public ParamCheckResource(ParamCheckService paramCheckService,
                              BscDataRepository bscDataRepository) {
        this.paramCheckService = paramCheckService;
        this.bscDataRepository = bscDataRepository;
    }

    @GetMapping("/check-param")
    public List<Map<String, Object>> queryParam(ParamCheckVM vm) throws ParseException {
        log.debug("进入一致性检查方法。");
        log.debug("视图模型: " + vm);
        return paramCheckService.queryParamData(vm);
    }

    @GetMapping("/check-bsc-by-cityId")
    public List<BscDataDTO> queryReport(String cityId){
        log.debug("查询bsc的区域id为：{}",cityId);
        Area area = new Area();
        area.setId(Long.parseLong(cityId));
        return bscDataRepository.findByAreaAndStatus(area,"N")
                .stream().map(BscDataMessageMapper.INSTANCE::bscDataToBscDataDto)
                .collect(Collectors.toList());
    }


}
