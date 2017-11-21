package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.domain.OriginFileAttr;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileAttrRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.LteMrDataService;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.LteMrDataImportDTO;
import com.hgicreate.rno.service.dto.LteMrDescDTO;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.vm.LteMrDataFileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteMrDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteMrDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/lte-mr-data")
public class LteMrDataResource {

    private final LteMrDataService lteMrDataService;

    private final OriginFileRepository originFileRepository;
    private  final OriginFileAttrRepository originFileAttrRepository;

    private final DataJobRepository dataJobRepository;
    private final DataJobReportRepository dataJobReportRepository;

    private final Environment env;

    public LteMrDataResource(LteMrDataService lteMrDataService, OriginFileRepository originFileRepository, OriginFileAttrRepository originFileAttrRepository,
                             DataJobRepository dataJobRepository, DataJobReportRepository dataJobReportRepository, Environment env) {
        this.lteMrDataService = lteMrDataService;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.env = env;
    }

    @PostMapping("/mr-import-query")
    public List<LteMrDataImportDTO> importQuery(LteMrDataImportVM vm) throws ParseException {
        log.debug("查询Mr数据文件导入记录。");
        log.debug("视图模型: " + vm);
        return lteMrDataService.queryImport(vm);
    }

    @GetMapping("/query-report")
    public List<DataJobReportDTO> queryReport(String id){
        log.debug("查询任务报告的任务id：{}",id);
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/data-query")
    public List<LteMrDescDTO> dataQuery(LteMrDataQueryVM vm) throws ParseException {
        log.debug("查询Mr数据记录。");
        log.debug("视图模型: " + vm);
        return lteMrDataService.dataQuery(vm);
    }

    /**
     * 接收上传文件并存储为本地文件
     *
     * @return 成功情况下返回 HTTP OK 状态，错误情况下返回 HTTP 4xx 状态。
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(LteMrDataFileUploadVM vm) {

        log.debug("模块名：" + vm.getModuleName());

        try {
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);

            //获取文件类型
            String fileExtension = filename.substring(filename.lastIndexOf("."),filename.length()).toLowerCase();
            String fileType = "ZIP";
            if((".csv").equals(fileExtension)){
                fileType = "CSV";
            }
            log.debug("上传的文件类型：{}",fileType);

            //获取文件大小
            int fileSize = (int) vm.getFile().getSize();
            log.debug("上传的文件大小：{}",fileSize);

            // 如果目录不存在则创建目录
            String directory = env.getProperty("rno.path.upload-files");
            File fileDirectory = new File(directory+"/"+vm.getModuleName());
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 以随机的 UUID 为文件名存储在本地
            filename = UUID.randomUUID().toString()+fileExtension;
            String filepath = Paths.get(directory+"/"+vm.getModuleName(), filename).toString();

            log.debug("存储的文件名：{}", filename);

            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();

            // 保存文件到FTP
            String ftpFullPath = FtpUtils.sendToFtp(vm.getModuleName(), filepath, true, env);
            log.debug("获取FTP文件的全路径：{}", ftpFullPath);

            //创建OriginFile对象，保存文件记录
            OriginFile originFile = new OriginFile();
            originFile.setFilename(vm.getFile().getOriginalFilename());
            originFile.setFileType(fileType);
            originFile.setFileSize(fileSize);
            originFile.setFullPath(filepath);
            originFile.setDataType("LTE-MR-DATA");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFile.setSourceType("上传");
            originFileRepository.save(originFile);

            //创建OriginFileAttr对象，保存文件属性
            OriginFileAttr originFileAttr = new OriginFileAttr();
            OriginFileAttr originFileAttr2 = new OriginFileAttr();
            originFileAttr.setName("record_date");
            originFileAttr.setValue(vm.getRecordDate());
            originFileAttr.setOriginFile(originFile);
            originFileAttrRepository.save(originFileAttr);
            originFileAttr2.setName("vendor");
            originFileAttr2.setValue(vm.getVendor());
            originFileAttr2.setOriginFile(originFile);
            originFileAttrRepository.save(originFileAttr2);

            //创建DataJob对象，创建文件任务
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));

            DataJob dataJob = new DataJob();
            dataJob.setName("MR测量数据导入");
            dataJob.setType("LTE-MR-DATA");
            dataJob.setPriority(1);
            dataJob.setArea(area);
            dataJob.setOriginFile(originFile);
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setCreatedDate(new Date());
            dataJob.setStatus("等待处理");
            dataJob.setDataStorePath(ftpFullPath);
            dataJob.setDataStoreType("FTP");
            dataJobRepository.save(dataJob);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
