package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmInterferMatrixJob;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmInterferMatrixJobRepository;
import com.hgicreate.rno.service.gsm.dto.GsmInterferMatrixJobDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForJobDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmInterferMatrixJobMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmNcsForJobMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmInterferMatrixVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsForJobVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GsmInterferMatrixService {
    private final GsmInterferMatrixJobRepository gsmInterferMatrixJobRepository;

    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    public GsmInterferMatrixService(GsmInterferMatrixJobRepository gsmInterferMatrixJobRepository,
                                    GsmEriNcsDescRepository gsmEriNcsDescRepository) {
        this.gsmInterferMatrixJobRepository = gsmInterferMatrixJobRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
    }

    public List<GsmInterferMatrixJobDTO> jobQuery(GsmInterferMatrixVM vm) throws ParseException {
        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegDate());
        Date endDate = sdf.parse(vm.getEndDate());
        List<GsmInterferMatrixJob> list;
        if("ALL".equals(vm.getInterMatrixType())){
            list = gsmInterferMatrixJobRepository.findTop1000ByAreaAndCreatedDateBetweenOrderByCreatedDateDesc(
                    area,beginDate,endDate);
        }else{
            list = gsmInterferMatrixJobRepository
                    .findTop1000ByAreaAndDataTypeAndAndCreatedDateBetweenOrderByCreatedDateDesc(
                    area,vm.getInterMatrixType(),beginDate,endDate);
        }
        return list.stream()
                .map(GsmInterferMatrixJobMapper.INSTANCE::gsmInterferMatrixToGsmInterferMatrixDTO)
                .collect(Collectors.toList());
    }

    public List<GsmNcsForJobDTO> ncsDataQuery(GsmNcsForJobVM vm) throws ParseException {
        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegMeaDate());
        Date endDate = sdf.parse(vm.getEndMeaDate());
        return gsmEriNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(area,beginDate,endDate)
                .stream()
                .map(GsmNcsForJobMapper.INSTANCE::ncsForJobToNcsForJobDTO)
                .collect(Collectors.toList());
    }

    // 更新任务状态
    public void runTask(Area area, GsmInterferMatrixJob job) {
        long timeInterval = (long) (Math.random() * (5000) + 60000);
        Runnable runnable = new Runnable() {
            boolean isStopped = false;
            public void run() {
                while (!isStopped) {
                    // ------- code for task to run
                    if ("".equals(job.getStatus()) || job.getStatus() == null || "排队中".equals(job.getStatus())) {
                        List<GsmInterferMatrixJob> list = gsmInterferMatrixJobRepository.findByAreaOrderByIdDesc(area);
                        if ("排队中".equals(list.get(1).getStatus()) || "正在计算".equals(list.get(1).getStatus())) {
                            job.setStatus("排队中");
                            gsmInterferMatrixJobRepository.save(job);
                            try {
                                Thread.sleep(timeInterval);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            job.setStartTime(new Date());
                            job.setStatus("正在计算");
                            gsmInterferMatrixJobRepository.save(job);
                            try {
                                Thread.sleep(timeInterval);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        job.setCompleteTime(new Date());
                        if (Math.random() < 0.1) {
                            job.setStatus("计算失败");
                        } else {
                            job.setStatus("计算成功");
                        }
                        gsmInterferMatrixJobRepository.save(job);
                        isStopped = true;
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
