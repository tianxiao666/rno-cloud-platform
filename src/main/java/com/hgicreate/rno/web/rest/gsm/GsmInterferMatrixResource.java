package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmInterferMatrixJob;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.gsm.GsmInterferMatrixJobRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmInterferMatrixService;
import com.hgicreate.rno.service.gsm.dto.GsmInterferMatrixJobDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForJobDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmInterferMatrixVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsForJobVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/gsm-interfer-matrix")
public class GsmInterferMatrixResource {
    private final GsmInterferMatrixService gsmInterferMatrixService;

    private final GsmInterferMatrixJobRepository gsmInterferMatrixJobRepository;
    private final AreaRepository areaRepository;

    public GsmInterferMatrixResource(GsmInterferMatrixService gsmInterferMatrixService,
                                     GsmInterferMatrixJobRepository gsmInterferMatrixJobRepository,
                                     AreaRepository areaRepository) {
        this.gsmInterferMatrixService = gsmInterferMatrixService;
        this.gsmInterferMatrixJobRepository = gsmInterferMatrixJobRepository;
        this.areaRepository = areaRepository;
    }

    @PostMapping("/job-query")
    public List<GsmInterferMatrixJobDTO> queryJob(GsmInterferMatrixVM vm) throws ParseException {
        log.debug("要查询的任务视图模型：{}", vm);
        return gsmInterferMatrixService.jobQuery(vm);
    }

    @PostMapping("/ncs-data-query")
    public List<GsmNcsForJobDTO> ncsDataQuery(GsmNcsForJobVM vm) throws ParseException {
        log.debug("查询NCS数据的视图模型：{}", vm);
        return gsmInterferMatrixService.ncsDataQuery(vm);
    }

    @PostMapping("/add-job")
    public boolean addInterferMatrixJob(GsmNcsForJobVM vm) throws ParseException {
        log.debug("干扰矩阵的任务数据视图模型：{}", vm);
        Area area = areaRepository.findById(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginMeaDate = sdf.parse(vm.getBegMeaDate());
        Date endMeaDate = sdf.parse(vm.getEndMeaDate());
        List<GsmNcsForJobDTO> dtoList = gsmInterferMatrixService.ncsDataQuery(vm);
        if (dtoList == null || dtoList.size() <= 0) {
            return false;
        }
        int recordCount = 0;
        for (GsmNcsForJobDTO ncs : dtoList) {
            recordCount += ncs.getRecordCount();
        }
        GsmInterferMatrixJob job = new GsmInterferMatrixJob();
        job.setArea(area);
        Calendar now = Calendar.getInstance();
        job.setName(area.getName() + now.get(Calendar.YEAR) + now.get(Calendar.MONTH) +
                "_" + now.get(Calendar.DATE) + "干扰矩阵计算");
        job.setPriority(1);
        job.setDataType("NCS");
        job.setBegMeaTime(beginMeaDate);
        job.setEndMeaTime(endMeaDate);
        List<GsmInterferMatrixJob> list = gsmInterferMatrixJobRepository.findByAreaOrderByIdDesc(area);
        if (list.size() <= 0) {
            job.setStartTime(now.getTime());
            job.setStatus("正在计算");
        }
        job.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        job.setCreatedDate(now.getTime());
        job.setRecordCount((long) recordCount);
        gsmInterferMatrixJobRepository.save(job);
        gsmInterferMatrixService.runTask(area, job);
        return true;
    }
}
