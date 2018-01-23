package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.DataJobReport;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.gsm.GsmCellDataService;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataDTO;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataFileDTO;
import com.hgicreate.rno.service.gsm.dto.GsmCellDescDTO;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCellDataImportVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCellDataVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCellDescVM;
import com.hgicreate.rno.web.rest.vm.FileUploadVM;
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
 * @author tao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-cell-data")
public class GsmCellDataResource {
    private final OriginFileRepository originFileRepository;
    private final DataJobRepository dataJobRepository;
    private final DataJobReportRepository dataJobReportRepository;

    private final GsmCellDataRepository gsmCellDataRepository;
    private final GsmCellDataService gsmCellDataService;

    private final Environment env;

    public GsmCellDataResource(OriginFileRepository originFileRepository, DataJobRepository dataJobRepository,
                               DataJobReportRepository dataJobReportRepository,
                               GsmCellDataRepository gsmCellDataRepository,
                               GsmCellDataService gsmCellDataService, Environment env) {
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.gsmCellDataService = gsmCellDataService;
        this.env = env;
    }

    /**
     * 根据页面查询条件查询文件导入记录
     * @param vm 接收页面查询条件的VM对象
     * @return 文件导入记录列表
     */
    @GetMapping("/query-import")
    public List<GsmCellDataFileDTO> queryImport(GsmCellDataImportVM vm) throws ParseException {
        log.debug("视图模型: " + vm);
        return gsmCellDataService.queryFileUploadRecord(vm);
    }

    /**
     * 根据任务id查询任务报告
     * @param id 任务id
     * @return 任务报告列表
     */
    @GetMapping("/query-report")
    public List<DataJobReportDTO> queryReportById(@RequestParam String id){
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据页面查询条件查询数据处理记录
     * @param vm 接收页面查询条件的VM对象
     * @return 数据处理记录列表
     */
    @GetMapping("/query-record")
    public List<GsmCellDescDTO> queryRecord(GsmCellDescVM vm) throws ParseException{
        log.debug("视图模型vm ={}", vm);
        return gsmCellDataService.queryRecord(vm);
    }

    /**
     * 上传工参数据文件
     * @param vm 接收文件属性的VM对象
     * @return 上传状态
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadGsmCellFile(FileUploadVM vm) {
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

            // 保存文件到FTP
            String ftpFullPath = FtpUtils.sendToFtp(vm.getModuleName(), filepath, true, env);
            log.debug("获取FTP文件的全路径：{}", ftpFullPath);

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("GSM小区工参数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));
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

    /**
     * 根据查询条件查询小区数据
     * @param vm 接收页面查询条件的VM对象
     * @return 小区数据列表
     */
    @GetMapping("/cell-query")
    public List<GsmCellDataDTO> cellQuery(GsmCellDataVM vm) {
        log.debug("查询条件：省={}，市={}，cellId={}, cell名称={},pci={}",
                vm.getProvinceId() ,
                vm.getCityId() ,
                vm.getCellId() ,
                vm.getCellName() ,
                vm.getBsc());
        return gsmCellDataService.queryGsmCell(vm);
    }

    /**
     * 根据小区id查询小区详细信息
     * @param cellId 小区id
     * @return 小区详细信息列表
     */
    @GetMapping("/cell-detail-id")
    public List<GsmCell> findCellDetailById(@RequestParam String cellId) {
        return gsmCellDataRepository.findByCellId(cellId);
    }

    /**
     * 根据小区id查询小区详细信息
     * @param cellId 小区id
     * @return 小区详细信息
     */
    @GetMapping("/cell-detail-edit")
    public GsmCell findCellDetailForEdit(@RequestParam String cellId) {
        return gsmCellDataRepository.findOne(cellId);
    }

    /**
     * 根据小区id删除小区
     * @param cellId 小区id
     */
    @PostMapping("/cell-delete")
    public void deleteByCellId(@RequestParam String cellId) {
        log.debug("待删除小区id为={}", cellId);
        gsmCellDataRepository.delete(cellId);
    }

    /**
     * 更新小区信息
     * @param gsmCell 小区信息
     * @return 更新状态
     */
    @PostMapping("/cell-detail-update")
    public boolean updateGsmCellDetail(GsmCell gsmCell) {
        try {
            log.debug("要更新的小区={}", gsmCell.getCellId());
            GsmCell lteCell =gsmCellDataRepository.findOne(gsmCell.getCellId());
            gsmCell.setCellName(lteCell.getCellName());
            gsmCell.setEnName(lteCell.getEnName());
            gsmCell.setArea(lteCell.getArea());
            gsmCell.setLastModifiedUser(SecurityUtils.getCurrentUserLogin());
            gsmCell.setLastModifiedDate(new Date());
            gsmCellDataRepository.save(gsmCell);
            return true;
        } catch (Exception e) {
            System.out.println("更新出错");
            log.error(e.getMessage());
            return false;
        }
    }
}
