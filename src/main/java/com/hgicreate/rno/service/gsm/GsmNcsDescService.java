package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileAttrRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmHwNcsDescRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmNcsDescQueryMapper;
import com.hgicreate.rno.service.mapper.DataJobMapper;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmUploadVM;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmNcsDescService {
    private final GsmHwNcsDescRepository gsmHwNcsDescRepository;
    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;
    private final DataJobRepository dataJobRepository;
    private  final OriginFileRepository originFileRepository;
    private final DataJobReportRepository dataJobReportRepository;
    private  final OriginFileAttrRepository originFileAttrRepository;
    private final Environment env;

    public GsmNcsDescService(
            GsmHwNcsDescRepository gsmHwNcsDescRepository, GsmEriNcsDescRepository gsmEriNcsDescRepository,
            DataJobRepository dataJobRepository, OriginFileRepository originFileRepository,
            DataJobReportRepository dataJobReportRepository, OriginFileAttrRepository originFileAttrRepository,
            Environment env) {
        this.gsmHwNcsDescRepository = gsmHwNcsDescRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
        this.dataJobRepository = dataJobRepository;
        this.originFileRepository = originFileRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.env = env;
    }

    public List<DataJobReportDTO> queryReport(String id){
        log.debug("查询任务报告的任务id：{}",id);
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    public List<GsmNcsDescQueryDTO> ncsDescQuery(GsmNcsDescQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        log.debug("开始时间：{}",vm.getBeginTestDate());
        log.debug("修改前的结束时间：{}",vm.getEndTestDate());
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        log.debug("修改后时间：{}",endDate);
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")) {
            if (Objects.equals(vm.getFactory(), "HW")) {
                return gsmHwNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(
                        area, vm.getBeginTestDate(), endDate)
                        .stream().map(GsmNcsDescQueryMapper.INSTANCE::hwNcsDescQueryToDTO)
                        .collect(Collectors.toList());
            }
            if (Objects.equals(vm.getFactory(), "ERI")) {
                return gsmEriNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(
                        area, vm.getBeginTestDate(), endDate)
                        .stream().map(GsmNcsDescQueryMapper.INSTANCE::eriNcsDescQueryToDTO)
                        .collect(Collectors.toList());
            }
        }
        if (Objects.equals(vm.getFactory(), "HW")) {
            return gsmHwNcsDescRepository.findTop1000ByAreaAndBscLikeAndMeaTimeBetween(
                    area,"%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate)
                    .stream().map(GsmNcsDescQueryMapper.INSTANCE::hwNcsDescQueryToDTO)
                    .collect(Collectors.toList());
        }
        if (Objects.equals(vm.getFactory(), "ERI")) {
            return gsmEriNcsDescRepository.findTop1000ByAreaAndBscLikeAndMeaTimeBetween(
                    area, "%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate)
                    .stream().map(GsmNcsDescQueryMapper.INSTANCE::eriNcsDescQueryToDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<GsmImportQueryDTO> gsmImportQuery(GsmImportQueryVM vm) {
        Calendar calendar = Calendar.getInstance();
        Area area = new Area();
        area.setId(vm.getAreaId());
        calendar.setTime(vm.getEndDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if ("全部".equals(vm.getStatus())) {
            return dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getBeginDate(), endDate, vm.getModuleName().toUpperCase())
                    .stream().map(DataJobMapper.INSTANCE::gsmImportQueryToDTO)
                    .collect(Collectors.toList());
        }
        return dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                area, vm.getStatus(), vm.getBeginDate(), endDate, vm.getModuleName().toUpperCase())
                .stream().map(DataJobMapper.INSTANCE::gsmImportQueryToDTO)
                .collect(Collectors.toList());
    }

    /**
     * NCS上传文件
     */
    public ResponseEntity<?> uploadFile(GsmUploadVM vm) {
        try {
            String filename = vm.getFile().getOriginalFilename();
            Date uploadBeginTime = new Date();
            // 获取文件名，并构建为本地文件路径
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
                originFile.setFileType("CSV");
                filename = UUID.randomUUID().toString() +".csv";
            }else if("application/x-zip-compressed".equals(vm.getFile().getContentType())){
                filename = UUID.randomUUID().toString() +".zip";
                originFile.setFileType("ZIP");
            }
            String filepath = Paths.get(directory + "/" + vm.getModuleName(), filename).toString();

            log.debug("存储的文件名：{}", filename);

            //更新文件记录RNO_ORIGIN_FILE
            originFile.setFilename(vm.getFile().getOriginalFilename());
            originFile.setFullPath(filepath);
            originFile.setFileSize((int)vm.getFile().getSize());
            originFile.setSourceType("上传");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFile.setDataType(vm.getModuleName().toUpperCase());
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
            dataJob.setName("NCS数据导入");
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
            dataJob.setType(vm.getModuleName().toUpperCase());
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
}
