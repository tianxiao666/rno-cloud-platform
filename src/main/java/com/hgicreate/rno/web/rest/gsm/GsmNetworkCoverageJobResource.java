package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.gsm.GsmNetworkCoverageJobRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmNetworkCoverageService;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForJobDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNetworkCoverageJobDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsForJobVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNetworkCoverageVM;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author tao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-network-coverage")
public class GsmNetworkCoverageJobResource {
    private final GsmNetworkCoverageService gsmNetworkCoverageService;

    private final GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository;
    private final AreaRepository areaRepository;

    public GsmNetworkCoverageJobResource(GsmNetworkCoverageService gsmNetworkCoverageService,
                                         GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository,
                                         AreaRepository areaRepository) {
        this.gsmNetworkCoverageService = gsmNetworkCoverageService;
        this.gsmNetworkCoverageJobRepository = gsmNetworkCoverageJobRepository;
        this.areaRepository = areaRepository;
    }

    /**
     * 网络覆盖分析任务查询
     * @param vm 接收页面查询条件的VM对象
     * @return 网络覆盖分析任务列表
     */
    @PostMapping("/job-query")
    public List<GsmNetworkCoverageJobDTO> jobQuery(GsmNetworkCoverageVM vm) throws ParseException {
        log.debug("视图模型：{}", vm);
        return gsmNetworkCoverageService.jobQuery(vm);
    }

    /**
     * 查询用于创建网络覆盖分析任务的ncs数据
     * @param vm 接收页面查询条件的VM对象
     * @return ncs数据列表
     */
    @PostMapping("/ncs-data-query")
    public List<GsmNcsForJobDTO> ncsDataQuery(GsmNcsForJobVM vm) throws ParseException {
        log.debug("视图模型：{}", vm);
        return gsmNetworkCoverageService.ncsDataQuery(vm);
    }

    /**
     * 新增网络覆盖分析任务
     * @param vm 新增网络覆盖分析任务的条件的VM对象
     * @return 新增任务结果
     */
    @PostMapping("/add-job")
    public boolean addNetCoverJob(GsmNcsForJobVM vm) throws ParseException {
        log.debug("视图模型：{}", vm);
        List<GsmNcsForJobDTO> dtoList = gsmNetworkCoverageService.ncsDataQuery(vm);
        if (dtoList == null || dtoList.size() <= 0) {
            return false;
        }
        Area area = areaRepository.findOne(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegMeaDate());
        Date endDate = sdf.parse(vm.getEndMeaDate());
        GsmNetworkCoverageJob job = new GsmNetworkCoverageJob();
        job.setArea(area);
        Date createdDate = new Date();
        job.setBegMeaTime(beginDate);
        job.setEndMeaTime(endDate);
        Calendar now = Calendar.getInstance();
        job.setName(area.getName() + now.get(Calendar.YEAR) + now.get(Calendar.MONTH) +
                "_" + now.get(Calendar.DATE) + "网络覆盖分析任务");
        job.setPriority(1);
        List<GsmNetworkCoverageJob> list = gsmNetworkCoverageJobRepository.findByAreaOrderByIdDesc(area);
        if (list.size() <= 0) {
            job.setStartTime(createdDate);
            job.setStatus("正在计算");
        }
        job.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        job.setCreatedDate(createdDate);
        int fileNumber = 0;
        for (GsmNcsForJobDTO ncs : dtoList) {
            fileNumber += ncs.getRecordCount();
        }
        job.setFileNumber(fileNumber);
        gsmNetworkCoverageJobRepository.save(job);
        gsmNetworkCoverageService.runTask(area, job);
        return true;
    }

    /**
     * 根据任务id下载任务结果文件
     * @param id 任务id
     * @return 成功情况下返回 HTTP OK 状态，错误情况下返回 HTTP 4xx 状态。
     */
    @GetMapping("/download-result")
    @ResponseBody
    public ResponseEntity<byte[]> downloadResultFile(String id) {
        log.debug("覆盖分析任务id：{}", id);
        GsmNetworkCoverageJob gsmNetworkCoverageJob = gsmNetworkCoverageJobRepository.findOne(Long.parseLong(id));
        Area area = gsmNetworkCoverageJob.getArea();
        File file = gsmNetworkCoverageService.saveGsmNetworkCoverageResult(id, area.getId());
        try {
            HttpHeaders headers = new HttpHeaders();
            String fileName = new String(file.getName().getBytes("UTF-8"),
                    "iso-8859-1");
            log.debug("覆盖分析结果文件名称：{}",fileName);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("覆盖分析结果下载出错：{}",e.getMessage());
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
