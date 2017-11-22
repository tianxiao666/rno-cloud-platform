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
import com.hgicreate.rno.service.LteTrafficDataService;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.LteTrafficDataDTO;
import com.hgicreate.rno.service.dto.LteTrafficDescDTO;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.vm.LteTrafficDataDescVM;
import com.hgicreate.rno.web.rest.vm.LteTrafficFileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteTrafficImportQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/api/lte-traffic-data")
public class LteTrafficDataResource {
    @Value("${rno.path.upload-files}")
    private String directory;

    private final LteTrafficDataService lteTrafficDataService;
    private final DataJobRepository dataJobRepository;
    private  final OriginFileRepository originFileRepository;
    private  final OriginFileAttrRepository originFileAttrRepository;
    private final DataJobReportRepository dataJobReportRepository;

    private final Environment env;

    public LteTrafficDataResource(LteTrafficDataService lteTrafficDataService, DataJobRepository dataJobRepository,
                                  DataJobReportRepository dataJobReportRepository,
                                  OriginFileRepository originFileRepository,
                                  OriginFileAttrRepository originFileAttrRepository,
                                  Environment env) {
        this.lteTrafficDataService = lteTrafficDataService;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.env = env;
    }

    @PostMapping("/query-import")
    public List<LteTrafficDataDTO> queryImport(LteTrafficImportQueryVM vm) throws ParseException {
        log.debug("查询 DT 文件导入记录。");
        log.debug("视图模型: " + vm);
        return lteTrafficDataService.queryTrafficData(vm);
    }

    @GetMapping("/query-report")
    public List<DataJobReportDTO> queryReport(String id){
        log.debug("查询任务报告的任务id：{}",id);
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/query-record")
    public List<LteTrafficDescDTO>  queryRecord(LteTrafficDataDescVM vm) throws ParseException{
        log.debug("视图模型vm ={}", vm);
        return lteTrafficDataService.queryRecord(vm);
    }

    /**
     * 接收上传文件并存储为本地文件
     *
     * @return 成功情况下返回 HTTP OK 状态，错误情况下返回 HTTP 4xx 状态。
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(LteTrafficFileUploadVM vm) {

        log.debug("模块名：" + vm.getModuleName());
        log.debug("视图模型: " + vm);

        try {
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);
            //创建更新对象
            OriginFile originFile = new OriginFile();

            // 如果目录不存在则创建目录
            File fileDirectory = new File(directory + "/" + vm.getModuleName());
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 以随机的 UUID 为文件名存储在本地
            if(filename.toUpperCase().endsWith("XML")){
                filename = UUID.randomUUID().toString() +".xml";
                originFile.setFileType("XML");
            }else if(filename.toUpperCase().endsWith("GZ")){
                filename = UUID.randomUUID().toString() +".gz";
                originFile.setFileType("GZ");
            }else if(filename.toUpperCase().endsWith("ZIP")){
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

            //更新文件记录RNO_ORIGIN_FILE_Attr
            OriginFileAttr originFileAttr = new OriginFileAttr();
            //更新文件记录RNO_ORIGIN_FILE_ATTR
            originFileAttr.setOriginFile(originFile);
            originFileAttr.setName("business_type");
            originFileAttr.setValue(vm.getBusiness_type());
            originFileAttrRepository.save(originFileAttr);

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
            dataJob.setName("网络统计数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setPriority(1);
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
}
