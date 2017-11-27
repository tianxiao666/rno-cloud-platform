package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.LteTrafficIndex;
import com.hgicreate.rno.repository.LteTrafficIndexRepository;
import com.hgicreate.rno.service.LteKpiQueryService;
import com.hgicreate.rno.service.dto.LteTrafficIndexDTO;
import com.hgicreate.rno.service.mapper.LteTrafficIndexMapper;
import com.hgicreate.rno.web.rest.vm.LteKpiQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/lte-kpi-query")
public class LteKpiQueryResource {

    private final LteTrafficIndexRepository lteTrafficIndexRepository;

    private final LteKpiQueryService lteKpiQueryService;

    public LteKpiQueryResource(LteTrafficIndexRepository lteTrafficIndexRepository, LteKpiQueryService lteKpiQueryService) {
        this.lteTrafficIndexRepository = lteTrafficIndexRepository;
        this.lteKpiQueryService = lteKpiQueryService;
    }

    @GetMapping("/load-index")
    public List<LteTrafficIndexDTO> getRno4GIndex() {
        log.debug("获取话统指标");
        List<LteTrafficIndex> list = lteTrafficIndexRepository.findAll();
        return list.stream()
                .map(LteTrafficIndexMapper.INSTANCE::trafficIndexToTrafficIndexDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/query-result")
    public List<Map<String, Object>> queryResult(LteKpiQueryVM vm) throws ParseException {
        log.debug("视图模型：" + vm);
        return lteKpiQueryService.queryResult(vm);
    }

    @PostMapping("/download-data")
    @ResponseBody
    public ResponseEntity<byte[]> downloadData(LteKpiQueryVM vm) throws ParseException {
        log.debug("视图模型：" + vm);
        File file = lteKpiQueryService.downloadData(vm);
        try {
            HttpHeaders headers = new HttpHeaders();
            String fileName = new String(file.getName().getBytes("UTF-8"),
                    "iso-8859-1");
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            if (file.delete()) {
                log.debug("临时文件删除成功。");
            } else {
                log.debug("临时文件删除失败。");
            }
        }
    }
}
