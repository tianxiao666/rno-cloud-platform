package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.domain.gsm.MrrDesc;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileAttrRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.MrrService;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.MrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.MrrImportQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.MrrUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-mrr-data")
public class MrrResource {
    private final MrrService mrrService;
    private final DataJobRepository dataJobRepository;
    private final DataJobReportRepository dataJobReportRepository;

    private  final OriginFileRepository originFileRepository;
    private  final OriginFileAttrRepository originFileAttrRepository;
    private final Environment env;

    public MrrResource(MrrService mrrService, DataJobRepository dataJobRepository, DataJobReportRepository dataJobReportRepository, OriginFileRepository originFileRepository, OriginFileAttrRepository originFileAttrRepository, Environment env) {
        this.mrrService = mrrService;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.env = env;
    }

    @PostMapping("/gsm-mrr-data-query")
    public List<MrrDesc> gsmMrrDateQuery(MrrDescQueryVM vm) {
        return mrrService.mrrDataQuery(vm);
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(MrrUploadVM vm) {
        try {
            Date uploadBeginTime = new Date();
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
            originFile.setSourceType("上传");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFile.setDataType(Constants.MRR_DATA_TYPE);
            originFileRepository.save(originFile);

            //更新文件记录RNO_ORIGIN_FILE_ATTR
            originFileAttr1.setOriginFile(originFile);
            originFileAttr1.setName("factory");
            originFileAttr1.setValue(vm.getImportFactory());
            originFileAttrRepository.save(originFileAttr1);

            originFileAttr2.setOriginFile(originFile);
            originFileAttr2.setName("test_time");
            SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
            originFileAttr2.setValue(sdf.format(vm.getFileDate()));
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
            dataJob.setName("MRR数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(vm.getCityId());
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setPriority(1);
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJob.setDataStoreType("FTP");
            dataJob.setDataStorePath(ftpFullPath);
            dataJob.setType(Constants.MRR_DATA_TYPE);
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

    @PostMapping("/mrr-import-query")
    public List<DataJob> mrrrImportQuery(MrrImportQueryVM vm){
        return mrrService.mrrImportQuery(vm);
    }
}
