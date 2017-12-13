package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.Cell;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${rno.lte.service.common:rno-lte-common-service}", fallback = CommonRestClientFallback.class)
public interface CommonRestClient {
    /*动态覆盖*/
    @GetMapping("/cellMgr/findByAreaId")
    public List<Cell> findByAreaId(@RequestParam("areaId") long areaId);

    /*PCI评估*/

    @PutMapping("/cellMgr/changeCellPci/{cellId}")
    public Boolean changeCellPci(@PathVariable("cellId") String cellId, @RequestParam("pci") int pci);
}
