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
import com.hgicreate.rno.service.LteDtDataService;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.LteDtDataFileDTO;
import com.hgicreate.rno.service.dto.LteDtDescDTO;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.vm.LteDtDescVM;
import com.hgicreate.rno.web.rest.vm.LteDtFileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteDtImportQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/api/lte-dt-data")
public class LteDtDataResource {

    private final LteDtDataService lteDtDataService;
    private final DataJobRepository dataJobRepository;
    private final DataJobReportRepository dataJobReportRepository;

    private  final OriginFileRepository originFileRepository;
    private  final OriginFileAttrRepository originFileAttrRepository;

    private final Environment env;

    public LteDtDataResource(LteDtDataService lteDtDataService,
                             OriginFileRepository originFileRepository,
                             OriginFileAttrRepository originFileAttrRepository,
                             DataJobRepository dataJobRepository,
                             DataJobReportRepository dataJobReportRepository,
                             Environment env) {
        this.lteDtDataService = lteDtDataService;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.env = env;
    }

    @PostMapping("/query-import")
    public List<LteDtDataFileDTO> queryImport(LteDtImportQueryVM vm) throws ParseException {
        log.debug("查询 DT 文件导入记录。");
        log.debug("视图模型: " + vm);
        return lteDtDataService.queryDataCollectDTOs(vm);
    }

    /**
     * 接收上传文件并存储为本地文件
     *
     * @return 成功情况下返回 HTTP OK 状态，错误情况下返回 HTTP 4xx 状态。
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(LteDtFileUploadVM vm) {

        log.debug("模块名：" + vm.getModuleName());
        log.debug("视图模型: " + vm);

        try {
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);
            //创建更新对象
            OriginFile originFile = new OriginFile();
            OriginFileAttr originFileAttr1 = new OriginFileAttr();
            OriginFileAttr originFileAttr2 = new OriginFileAttr();
            // 如果目录不存在则创建目录
            String directory = env.getProperty("rno.path.upload-files");
            File fileDirectory = new File(directory + "/" + vm.getModuleName());
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 以随机的 UUID 为文件名存储在本地
            if("application/vnd.ms-excel".equals(vm.getFile().getContentType())){
                filename = UUID.randomUUID().toString() +".csv";
                originFile.setFileType("CSV");
            }else if("application/x-zip-compressed".equals(vm.getFile().getContentType())){
                filename = UUID.randomUUID().toString() +".zip";
                originFile.setFileType("ZIP");
            }
            String filepath = Paths.get(directory + "/" + vm.getModuleName(), filename).toString();

            log.debug("存储的文件名：{}", filename);

            //更新文件记录RNO_ORIGIN_FILE
            originFile.setFilename(vm.getFile().getOriginalFilename());
            originFile.setDataType(vm.getModuleName().toUpperCase());
            originFile.setFullPath(filepath);
            originFile.setFileSize((int)vm.getFile().getSize());
            originFile.setSourceType(vm.getSourceType());
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFileRepository.save(originFile);

            //更新文件记录RNO_ORIGIN_FILE_ATTR
            originFileAttr1.setOriginFile(originFile);
            originFileAttr1.setName("area_type");
            originFileAttr1.setValue(vm.getArea_type());
            originFileAttrRepository.save(originFileAttr1);

            originFileAttr2.setOriginFile(originFile);
            originFileAttr2.setName("business_type");
            originFileAttr2.setValue(vm.getBusiness_type());
            originFileAttrRepository.save(originFileAttr2);



            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();

            // 保存文件到FTP
            String ftpFullPath = FtpUtils.sendToFtp(vm.getModuleName(), filepath, true, env);
            log.debug("获取FTP文件的全路径：{}", ftpFullPath);

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("路测数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setPriority(2);
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJob.setDataStoreType("FTP");
            dataJob.setDataStorePath(ftpFullPath);
            dataJobRepository.save(dataJob);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/query-import-detail-id")
    public List<DataJobReportDTO> queryImportDetailById(@RequestParam String id){
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/query-record")
    public List<LteDtDescDTO> queryRecord(LteDtDescVM vm) throws ParseException{
        log.debug("视图模型vm={}",vm);
        return lteDtDataService.queryRecord(vm);
    }
}
