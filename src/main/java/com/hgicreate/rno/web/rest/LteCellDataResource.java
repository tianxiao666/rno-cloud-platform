package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.repository.LteCellDataRepository;
import com.hgicreate.rno.service.LteCellDataService;
import com.hgicreate.rno.service.dto.DataCollectDTO;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.service.dto.LteCellDataRecordDTO;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-data")
public class LteCellDataResource {

    @Value("${rno.path.upload-files}")
    private String directory;

    private final LteCellDataRepository lteCellDataRepository;

    private final LteCellDataService lteCellDataService;

    public LteCellDataResource(LteCellDataRepository lteCellDataRepository, LteCellDataService lteCellDataService) {
        this.lteCellDataRepository = lteCellDataRepository;
        this.lteCellDataService = lteCellDataService;
    }

    @PostMapping("/cell-query")
    public List<LteCellDataDTO> cellQuery(LteCellDataVM lteCellDataVM) {
        log.debug("查询条件：省="
                + lteCellDataVM.getProvinceId() + ",市="
                + lteCellDataVM.getCityId() + "，LTE CELL ID="
                + lteCellDataVM.getCellId() + "，LTE CELL名称="
                + lteCellDataVM.getCellName() + "，小区PCI="
                + lteCellDataVM.getPci());
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
            String fileName = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", fileName);

            // 如果目录不存在则创建目录
            File file = new File(directory + "/" + vm.getModuleName());
            if (!file.exists() && !file.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // 以随机的 UUID 为文件名存储在本地
            fileName = UUID.randomUUID().toString();
            String filepath = Paths.get(directory+ "/" + vm.getModuleName(), fileName).toString();
            log.debug("存储的文件名：{}", fileName);
            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/query-import")
    public List<DataCollectDTO> queryImport(LteCellDataVM vm) {
        log.debug("视图模型: " + vm);
        return lteCellDataService.queryFileUploadRecord();
    }

    @PostMapping("/query-record")
    public List<LteCellDataRecordDTO> queryRecord(){
        return lteCellDataService.queryRecord();
    }
}
