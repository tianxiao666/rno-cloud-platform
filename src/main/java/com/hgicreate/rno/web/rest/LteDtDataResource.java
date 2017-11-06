package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.DataCollectService;
import com.hgicreate.rno.service.dto.DataCollectDTO;
import com.hgicreate.rno.web.rest.vm.FileUploadVM;
import com.hgicreate.rno.web.rest.vm.ImportQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/lte-dt-data")
public class LteDtDataResource {

    @Value("${rno.path.upload-files}")
    private String directory;

    private final DataCollectService dataCollectService;

    public LteDtDataResource(DataCollectService dataCollectService) {
        this.dataCollectService = dataCollectService;
    }

    @PostMapping("/query-import")
    public List<DataCollectDTO> queryImport(ImportQueryVM vm) {
        log.debug("查询 DT 文件导入记录。");
        log.debug("视图模型: " + vm);
        return dataCollectService.queryDataCollectDTOs();
    }

    /**
     * 接收上传文件并存储为本地文件
     * @return 成功情况下返回 HTTP OK 状态，错误情况下返回 HTTP 4xx 状态。
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(FileUploadVM vm) {

        log.debug("模块名：" + vm.getModuleName());
        log.debug("上传文件处理中...");

        try {
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
            log.debug("filename = {}", filename);

            // 以随机的 UUID 为文件名存储在本地
            filename = UUID.randomUUID().toString();
            String filepath = Paths.get(directory, filename).toString();

            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
