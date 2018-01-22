package com.hgicreate.rno.web.rest.gsm;


import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileAttrRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.gsm.GsmTrafficDataService;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficDataDescDTO;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficDataFileDTO;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficDataDescVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficDataImportVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficFileUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author yang.ch1
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-traffic-data")
public class GsmTrafficDataResource {

    private final Environment env;

    private final OriginFileRepository originFileRepository;

    private final OriginFileAttrRepository originFileAttrRepository;

    private final DataJobRepository dataJobRepository;

    private final DataJobReportRepository dataJobReportRepository;

    private final GsmTrafficDataService gsmTrafficDataService;

    public GsmTrafficDataResource(Environment env, OriginFileRepository originFileRepository, OriginFileAttrRepository originFileAttrRepository, DataJobRepository dataJobRepository, DataJobReportRepository dataJobReportRepository, GsmTrafficDataService gsmTrafficDataService) {
        this.env = env;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.gsmTrafficDataService = gsmTrafficDataService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadLteCellFile(GsmTrafficFileUploadVM vm) {
        log.debug("模块名：" + vm.getModuleName());
        try {
            Date uploadBeginTime = new Date();
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);
            OriginFile originFile = new OriginFile();
            originFile.setFilename(filename);
            // 如果目录不存在则创建目录
            String directory = env.getProperty("rno.path.upload-files");
            File file = new File(directory + "/" + vm.getModuleName());
            if (!file.exists() && !file.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 以随机的 UUID 为文件名存储在本地
            if("application/vnd.ms-excel".equals(vm.getFile().getContentType())){
                filename = UUID.randomUUID().toString() +".csv";
                originFile.setFileType("CSV");
            }else{
                filename = UUID.randomUUID().toString() +".zip";
                originFile.setFileType("ZIP");
            }

            String filepath = Paths.get(directory+ "/" + vm.getModuleName(), filename).toString();
            log.debug("存储的文件名：{}", filename);

            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();

            //更新文件记录
            originFile.setDataType(vm.getModuleName().toUpperCase());
            originFile.setFullPath(filepath);
            originFile.setFileSize((int)vm.getFile().getSize());
            originFile.setSourceType("上传");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFileRepository.save(originFile);

            OriginFileAttr originFileAttr = new OriginFileAttr();
            originFileAttr.setOriginFile(originFile);
            originFileAttr.setName("indicatorType" );
            originFileAttr.setValue(vm.getIndicatorType());

            OriginFileAttr originFileAttr1 = new OriginFileAttr();
            originFileAttr1.setOriginFile(originFile);
            originFileAttr1.setName("handleReplicateMethod" );
            originFileAttr1.setValue(vm.getHandleReplicateMethod());

            OriginFileAttr originFileAttr2 = new OriginFileAttr();
            originFileAttr2.setOriginFile(originFile);
            originFileAttr2.setName("areaId" );
            originFileAttr2.setValue(vm.getAreaId());
            originFileAttrRepository.save(originFileAttr);
            originFileAttrRepository.save(originFileAttr1);
            originFileAttrRepository.save(originFileAttr2);

            // 保存文件到FTP
            String ftpFullPath = FtpUtils.sendToFtp(vm.getModuleName(), filepath, true, env);
            log.debug("获取FTP文件的全路径：{}", ftpFullPath);

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("GSM话务统计数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getCityId()));
            dataJob.setArea(area);
            dataJob.setPriority(1);
            dataJob.setCreatedDate(new Date());
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJob.setDataStoreType("FTP");
            dataJob.setDataStorePath(ftpFullPath);
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

    @GetMapping("/query-import")
    public List<GsmTrafficDataFileDTO> queryImport(GsmTrafficDataImportVM vm) throws ParseException {
        log.debug("视图模型: " + vm);
        return gsmTrafficDataService.queryFileUploadRecord(vm);
    }

    @GetMapping("/query-report")
    public List<DataJobReportDTO> queryReportById(@RequestParam String id){
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/query-traffic-desc")
    public List<GsmTrafficDataDescDTO> queryTrafficDesc(GsmTrafficDataDescVM vm) throws ParseException{
        log.debug("进入查询话务数据记录方法，vm={}",vm);
        return gsmTrafficDataService.queryTrafficDataDesc(vm);
    }
}
