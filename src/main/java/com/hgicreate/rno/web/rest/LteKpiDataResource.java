package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.LteKpiDataService;
import com.hgicreate.rno.service.dto.LteKpiDataFileDTO;
import com.hgicreate.rno.service.dto.LteKpiDataRecordDTO;
import com.hgicreate.rno.web.rest.vm.FileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteKpiDataFileVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/lte-kpi-data")
public class LteKpiDataResource {

    @Value("${rno.path.upload-files}")
    private String directory;

    private final OriginFileRepository originFileRepository;

    private final DataJobRepository dataJobRepository;

    private final LteKpiDataService lteKpiDataService;

    public LteKpiDataResource(OriginFileRepository originFileRepository, DataJobRepository dataJobRepository, LteKpiDataService lteKpiDataService) {
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
        this.lteKpiDataService = lteKpiDataService;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadLteCellFile(FileUploadVM vm) {
        log.debug("模块名：" + vm.getModuleName());
        try {
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);
            OriginFile originFile = new OriginFile();
            originFile.setFilename(filename);
            // 如果目录不存在则创建目录
            File file = new File(directory + "/" + vm.getModuleName());
            if (!file.exists() && !file.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 以随机的 UUID 为文件名存储在本地
            if(filename.endsWith(".csv")){
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

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("KPI数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJobRepository.save(dataJob);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/query-import")
    public List<LteKpiDataFileDTO> queryImport(LteKpiDataFileVM vm) throws ParseException {
        log.debug("视图模型: " + vm);
        return lteKpiDataService.queryFileUploadRecord(vm);
    }

    @PostMapping("/query-record")
    public List<LteKpiDataRecordDTO> queryRecord(){
        return lteKpiDataService.queryRecord();
    }
}
