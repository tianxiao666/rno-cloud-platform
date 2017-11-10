package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.repository.NcellRepository;
import com.hgicreate.rno.service.LteNcellRelationService;
import com.hgicreate.rno.service.dto.DataCollectDTO;
import com.hgicreate.rno.service.dto.LteNcellImportDtDTO;
import com.hgicreate.rno.service.dto.LteNcellRelationDTO;
import com.hgicreate.rno.web.rest.vm.FileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteNcellImportDtQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellImportQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellRelationQueryVM;
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
@RequestMapping("/api/lte-ncell-relation")
public class LteNcellRelationResource {

    @Value("${rno.path.upload-files}")
    private String directory;

    private final LteNcellRelationService lteNcellRelationService;
    private final NcellRepository ncellRepository;

    public LteNcellRelationResource(NcellRepository ncellRepository, LteNcellRelationService lteNcellRelationService) {
        this.ncellRepository = ncellRepository;
        this.lteNcellRelationService = lteNcellRelationService;
    }

    @PostMapping("/ncell-query")
    public List<LteNcellRelationDTO> ncellQuery(LteNcellRelationQueryVM vm) {
        log.debug("查询LTE邻区关系");
        return lteNcellRelationService.queryNcellRelationDTOs(vm);
    }

    @PostMapping("/ncell-import-query")
    public List<DataCollectDTO> importQuery(LteNcellImportQueryVM vm){
        log.debug("查询 DT 文件导入记录。");
        log.debug("视图模型: " + vm);
        return lteNcellRelationService.queryImport(vm);
    }

    @PostMapping("/ncell-import-data-query")
    public List<LteNcellImportDtDTO> ncellDataQuery(LteNcellImportDtQueryVM vm){
        log.debug("查询 DT 文件导入记录。");
        log.debug("视图模型: " + vm);
        return lteNcellRelationService.queryImportDt(vm);
    }

    @GetMapping("/deleteByCellIdAndNcellId")
    public void deleteByCellId(@RequestParam long id){
        log.debug("待删除邻区id为={}", id);
        ncellRepository.delete(id);
    }

    /**
     * 接收上传文件并存储为本地文件
     *
     * @return 成功情况下返回 HTTP OK 状态，错误情况下返回 HTTP 4xx 状态。
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(FileUploadVM vm) {

        log.debug("模块名：" + vm.getModuleName());

        try {
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
            log.debug("上传的文件名：{}", filename);

            // 如果目录不存在则创建目录
            directory += "/"+vm.getModuleName();
            System.out.println(directory);
            File fileDirectory = new File(directory);
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 以随机的 UUID 为文件名存储在本地
            filename = UUID.randomUUID().toString()+".csv";
            String filepath = Paths.get(directory, filename).toString();

            log.debug("存储的文件名：{}", filename);

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
}
