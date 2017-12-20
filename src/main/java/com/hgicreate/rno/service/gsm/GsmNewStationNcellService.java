package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.DataJobReport;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.repository.gsm.GsmLteNcellDescRepository;
import com.hgicreate.rno.repository.gsm.GsmNcellDescRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.dto.GsmNewStationNcellDescQueryDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmNewStationNcellDescQueryMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNewStationNcellDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNewStationNcellUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmNewStationNcellService {
    private final GsmNcellDescRepository gsmNcellDescRepository;
    private final GsmLteNcellDescRepository gsmLteNcellDescRepository;
    private final OriginFileRepository originFileRepository;
    private final DataJobRepository dataJobRepository;
    private final DataJobReportRepository dataJobReportRepository;
    private final Environment env;
    public GsmNewStationNcellService(GsmNcellDescRepository gsmNcellDescRepository, GsmLteNcellDescRepository gsmLteNcellDescRepository, OriginFileRepository originFileRepository, DataJobRepository dataJobRepository, DataJobReportRepository dataJobReportRepository, Environment env) {
        this.gsmNcellDescRepository = gsmNcellDescRepository;
        this.gsmLteNcellDescRepository = gsmLteNcellDescRepository;
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.env = env;
    }

    public ResponseEntity<?> gsmNewStationNcellUpload(GsmNewStationNcellUploadVM vm) {
        try {
            Date uploadBeginTime = new Date();
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);
            //创建更新对象
            OriginFile originFile = new OriginFile();
            // 如果目录不存在则创建目录
            String directory = env.getProperty("rno.path.upload-files");
            File fileDirectory = new File(directory + "/" + vm.getFileCode());
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 以随机的 UUID 为文件名存储在本地
            filename = UUID.randomUUID().toString() + ".csv";
            originFile.setFileType("CSV");

            String filepath = Paths.get(directory + "/" + vm.getFileCode(), filename).toString();

            log.debug("存储的文件名：{}", filename);

            //更新文件记录RNO_ORIGIN_FILE
            originFile.setFilename(vm.getFile().getOriginalFilename());
            originFile.setDataType(vm.getFileCode().toUpperCase());
            originFile.setFullPath(filepath);
            originFile.setFileSize((int) vm.getFile().getSize());
            originFile.setSourceType("上传");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFile.setDataType(vm.getFileCode());
            originFileRepository.save(originFile);

            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();

            // 保存文件到FTP
            String ftpFullPath = FtpUtils.sendToFtp(vm.getFileCode(), filepath, true, env);
            log.debug("获取FTP文件的全路径：{}", ftpFullPath);

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("新站数据导入");
            dataJob.setType(vm.getFileCode().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(vm.getAreaId());
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setPriority(1);
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJob.setDataStoreType("FTP");
            dataJob.setDataStorePath(ftpFullPath);
            dataJob.setType(vm.getFileCode());
            dataJobRepository.save(dataJob);
            //建立任务报告
            DataJobReport dataJobReport = new DataJobReport();
            dataJobReport.setDataJob(dataJob);
            dataJobReport.setStage("文件上传");
            dataJobReport.setStartTime(uploadBeginTime);
            dataJobReport.setCompleteTime(new Date());
            dataJobReport.setStatus("成功");
            dataJobReport.setMessage("文件成功上传至服务器");
            dataJobReportRepository.save(dataJobReport);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<GsmNewStationNcellDescQueryDTO> descQuery(GsmNewStationNcellDescQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (Objects.equals(vm.getDataType(), "GSM-NEW-STATION-NCELL")){
            return gsmNcellDescRepository.findTop1000ByAreaAndCreatedDateBetween(area, vm.getBeginTestDate(), endDate)
                    .stream().map(GsmNewStationNcellDescQueryMapper.INSTANCE::gsmDescToDTO)
                    .collect(Collectors.toList());
        }
        if (Objects.equals(vm.getDataType(), "LTE-NEW-STATION-NCELL")) {
            return gsmLteNcellDescRepository.findTop1000ByAreaAndCreatedDateBetween(area, vm.getBeginTestDate(), endDate)
                    .stream().map(GsmNewStationNcellDescQueryMapper.INSTANCE::lteDescToDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
