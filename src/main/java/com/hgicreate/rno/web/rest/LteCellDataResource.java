package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.repository.*;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.LteCellDataService;
import com.hgicreate.rno.service.dto.*;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.vm.LteCellDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import com.hgicreate.rno.web.rest.vm.FileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteCellDescVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-data")
public class LteCellDataResource {

    private final OriginFileRepository originFileRepository;

    private final DataJobRepository dataJobRepository;

    private final DataJobReportRepository dataJobReportRepository;

    private final LteCellDataRepository lteCellDataRepository;

    private final LteCellDataService lteCellDataService;

    private final Environment env;

    public LteCellDataResource(OriginFileRepository originFileRepository,
                               DataJobRepository dataJobRepository, DataJobReportRepository dataJobReportRepository, LteCellDataRepository lteCellDataRepository,
                               LteCellDataService lteCellDataService, Environment env) {
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.lteCellDataRepository = lteCellDataRepository;
        this.lteCellDataService = lteCellDataService;
        this.env = env;
    }

    @PostMapping("/cell-query")
    public List<LteCellDataDTO> cellQuery(LteCellDataVM lteCellDataVM) {
        log.debug("查询条件：省={}，市={}，cellId={}, cell名称={},pci={}",
                lteCellDataVM.getProvinceId() ,
                lteCellDataVM.getCityId() ,
                lteCellDataVM.getCellId() ,
                lteCellDataVM.getCellName() ,
                lteCellDataVM.getPci());
        return lteCellDataService.queryLteCell(lteCellDataVM);
    }

    @GetMapping("/cell-detail-Id")
    public List<Cell> findCellDetailById(@RequestParam String cellId) {
        String enodebId = lteCellDataRepository.findOne(cellId).getEnodebId();
        return lteCellDataRepository.findByEnodebId(enodebId);
    }

    @GetMapping("/cell-detail-edit")
    public Cell findCellDetailForEdit(@RequestParam String cellId) {
        return lteCellDataRepository.findOne(cellId);
    }

    @GetMapping("/cell-delete")
    public void deleteByCellId(@RequestParam String cellId) {
        log.debug("待删除小区id为={}", cellId);
        lteCellDataRepository.delete(cellId);
    }

    @PostMapping("/cell-detail-update")
    public boolean updateLteCellDetail(Cell cell) {
        try {
            log.debug("要更新的小区={}", cell.getLatitude());
            cell.setLastModifiedUser(SecurityUtils.getCurrentUserLogin());
            cell.setLastModifiedDate(new Date());
            lteCellDataRepository.save(cell);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
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
            dataJob.setName("小区工参数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJob.setDataStoreType("ftp");
            dataJob.setDataStorePath(ftpFullPath);
            dataJobRepository.save(dataJob);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/query-import")
    public List<LteCellDataFileDTO> queryImport(LteCellDataImportVM vm) throws ParseException {
        log.debug("视图模型: " + vm);
        return lteCellDataService.queryFileUploadRecord(vm);
    }

    @PostMapping("/query-import-detail-id")
    public List<DataJobReportDTO> queryImportDetailById(@RequestParam String id){
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/query-record")
    public List<LteCellDescDTO> queryRecord(LteCellDescVM vm) throws ParseException{

        log.debug("视图模型vm ={}", vm);
        return lteCellDataService.queryRecord(vm);
    }
}
