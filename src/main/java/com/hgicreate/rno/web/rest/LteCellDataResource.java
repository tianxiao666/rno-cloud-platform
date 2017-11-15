package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteCellDataRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.LteCellDataService;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.service.dto.LteCellDataFileDTO;
import com.hgicreate.rno.service.dto.LteCellDataRecordDTO;
import com.hgicreate.rno.web.rest.vm.LteCellDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import com.hgicreate.rno.web.rest.vm.FileUploadVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-data")
public class LteCellDataResource {

    @Value("${rno.path.upload-files}")
    private String directory;

    private final OriginFileRepository originFileRepository;

    private final DataJobRepository dataJobRepository;

    private final LteCellDataRepository lteCellDataRepository;

    private final LteCellDataService lteCellDataService;

    public LteCellDataResource(OriginFileRepository originFileRepository,
                               DataJobRepository dataJobRepository, LteCellDataRepository lteCellDataRepository,
                               LteCellDataService lteCellDataService) {
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
        this.lteCellDataRepository = lteCellDataRepository;
        this.lteCellDataService = lteCellDataService;
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

    @GetMapping("/findCellDetailById")
    public List<Cell> findCellDetailById(@RequestParam String cellId) {
        String enodebId = lteCellDataRepository.findOne(cellId).getEnodebId();
        return lteCellDataRepository.findByEnodebId(enodebId);
    }

    @GetMapping("/findCellDetailForEdit")
    public Cell findCellDetailForEdit(@RequestParam String cellId) {
        return lteCellDataRepository.findOne(cellId);
    }

    @GetMapping("/deleteByCellId")
    public void deleteByCellId(@RequestParam String cellId) {
        log.debug("待删除小区id为={}", cellId);
        lteCellDataRepository.delete(cellId);
    }

    @PostMapping("/updateLteCellDetail")
    public boolean updateLteCellDetail(Cell cell) {
        try {
            log.debug("要更新的小区={}", cell.getLatitude());
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
            originFile.setFileSize(filepath.getBytes().length);
            originFile.setSourceType("上传");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFileRepository.save(originFile);

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

    @PostMapping("/query-record")
    public List<LteCellDataRecordDTO> queryRecord(){
        return lteCellDataService.queryRecord();
    }
}
