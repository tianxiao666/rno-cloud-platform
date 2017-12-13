package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.Cell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class CommonRestClientFallback implements CommonRestClient {

    /* 动态覆盖 */

    @Override
    public List<Cell> findByAreaId(long areaId) {
        log.info("通过区域ID获取小区参数信息失败。");
        return Collections.emptyList();
    }

    /* PCI评估 */

    @PutMapping("/changeCellPci/{cellId}")
    public Boolean changeCellPci(@PathVariable("cellId") String cellId, int pci) {
        log.info("更改小区PCI信息失败。");
        return Boolean.FALSE;
    }
}
