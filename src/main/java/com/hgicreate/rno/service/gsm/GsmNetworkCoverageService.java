package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmNetworkCoverageJobRepository;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForJobDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNetworkCoverageJobDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmNcsForJobMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmNetworkCoverageJobMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsForJobVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNetworkCoverageVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author tao.xj
 */
@Slf4j
@Service
public class GsmNetworkCoverageService {
    private final GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository;

    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    private final GsmCellDataRepository gsmCellDataRepository;
    private final AreaRepository areaRepository;

    public GsmNetworkCoverageService(GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository,
                                     GsmEriNcsDescRepository gsmEriNcsDescRepository,
                                     GsmCellDataRepository gsmCellDataRepository, AreaRepository areaRepository) {
        this.gsmNetworkCoverageJobRepository = gsmNetworkCoverageJobRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.areaRepository = areaRepository;
    }

    public List<GsmNetworkCoverageJobDTO> jobQuery(GsmNetworkCoverageVM vm) throws ParseException {

        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegDate());
        Date endDate = sdf.parse(vm.getEndDate());
        List<GsmNetworkCoverageJob> list = gsmNetworkCoverageJobRepository
                .findTop1000ByAreaAndCreatedDateBetweenOrderByCreatedDateDesc(area, beginDate, endDate);
        return list.stream()
                .map(GsmNetworkCoverageJobMapper.INSTANCE::gsmNetworkCoverageToGsmNetworkCoverageDTO)
                .collect(Collectors.toList());
    }

    public List<GsmNcsForJobDTO> ncsDataQuery(GsmNcsForJobVM vm) throws ParseException {
        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegMeaDate());
        Date endDate = sdf.parse(vm.getEndMeaDate());
        return gsmEriNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(area, beginDate, endDate)
                .stream()
                .map(GsmNcsForJobMapper.INSTANCE::ncsForJobToNcsForJobDTO)
                .collect(Collectors.toList());
    }

    // 生成结果文件

    /**
     * 生成结果文件
     * @param jobId 任务id
     * @param cityId 城市id
     * @return 要下载的结果文件
     */
    public File saveGsmNetworkCoverageResult(String jobId, Long cityId) {
        List<GsmCell> cellList = gsmCellDataRepository.findByArea_Id(cityId);
        Area area = areaRepository.findOne(cityId);
        String directory = System.getProperty("java.io.tmpdir");
        File fileDirectory = new File(directory);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        File csvFile = new File(directory + area.getName() + "_" + jobId + "_2G方向角.csv");
        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csvFile, true), "gbk"));
            //标题
            bw.write("城市,小区名,CELLID,现网方向角,计算方向角,方向角差值");
            bw.newLine();
            String cityName = area.getName();
            Random random = new Random();
            for (GsmCell gsmCell : cellList) {
                String cellName = gsmCell.getCellName();
                String cellId = gsmCell.getCellId();
                int calAzimuth = random.nextInt(360);
                String line;
                if (gsmCell.getAzimuth() == null) {
                    int diffAzimuth = calAzimuth > 180 ? Math.abs(360 - calAzimuth) : calAzimuth;
                    line = "\"" + cityName
                            + "\",\"" + cellName
                            + "\",\"" + cellId
                            + "\",\"" + ""
                            + "\",\"" + calAzimuth
                            + "\",\"" + diffAzimuth + "\"";
                } else {
                    int currentAzimuth = gsmCell.getAzimuth();
                    int diffAzimuth = Math.abs(calAzimuth - currentAzimuth);
                    int curAzimuthInt = diffAzimuth > 180 ? 360 - diffAzimuth : diffAzimuth;
                    line = "\"" + cityName
                            + "\",\"" + cellName
                            + "\",\"" + cellId
                            + "\",\"" + currentAzimuth
                            + "\",\"" + calAzimuth
                            + "\",\"" + curAzimuthInt + "\"";
                }
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                }
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 更新任务状态
     *
     * @param area 地区
     * @param job  网络覆盖分析任务对象
     */
    @Async
    public void runTask(Area area, GsmNetworkCoverageJob job) {
        log.debug("进入定时任务");
        long timeInterval = (long) (Math.random() * (5000) + 60000);
        boolean isStopped = false;
        while (!isStopped) {
            if ("".equals(job.getStatus()) || job.getStatus() == null || "排队中".equals(job.getStatus())) {
                List<GsmNetworkCoverageJob> list = gsmNetworkCoverageJobRepository.findByAreaOrderByIdDesc(area);
                if ("排队中".equals(list.get(1).getStatus()) || "正在计算".equals(list.get(1).getStatus())) {
                    job.setStatus("排队中");
                    gsmNetworkCoverageJobRepository.save(job);
                    log.debug("覆盖分析任务状态更新：{}", job.getStatus());
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    job.setStartTime(new Date());
                    job.setStatus("正在计算");
                    gsmNetworkCoverageJobRepository.save(job);
                    log.debug("覆盖分析任务状态更新：{}", job.getStatus());
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                job.setCompleteTime(new Date());
                if (Math.random() < 0.1) {
                    job.setStatus("异常终止");
                } else {
                    job.setStatus("正常完成");
                }
                gsmNetworkCoverageJobRepository.save(job);
                log.debug("覆盖分析任务状态更新：{}", job.getStatus());
                isStopped = true;
            }
        }
    }
}
