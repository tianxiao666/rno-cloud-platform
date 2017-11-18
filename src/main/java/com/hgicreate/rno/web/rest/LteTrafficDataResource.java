package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.LteTrafficDataService;
import com.hgicreate.rno.service.dto.LteTrafficDataDTO;
import com.hgicreate.rno.web.rest.vm.LteTrafficFileUploadVM;
import com.hgicreate.rno.web.rest.vm.LteTrafficImportQueryVM;
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
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/lte-traffic-data")
public class LteTrafficDataResource {
    @Value("${rno.path.upload-files}")
    private String directory;

    private final LteTrafficDataService lteTrafficDataService;
    private final DataJobRepository dataJobRepository;
    private  final OriginFileRepository originFileRepository;

    public LteTrafficDataResource(LteTrafficDataService lteTrafficDataService, DataJobRepository dataJobRepository, OriginFileRepository originFileRepository) {
        this.lteTrafficDataService = lteTrafficDataService;
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
    }

    @PostMapping("/query-import")
    public List<LteTrafficDataDTO> queryImport(LteTrafficImportQueryVM vm) throws ParseException {
        log.debug("查询 DT 文件导入记录。");
        log.debug("视图模型: " + vm);
        return lteTrafficDataService.queryTrafficDataCollectDTOs(vm);
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
            if(filename.endsWith("xml")){
                filename = UUID.randomUUID().toString() +".xml";
                originFile.setFileType("XML");
            }else if(filename.endsWith("gz")){
                filename = UUID.randomUUID().toString() +".gz";
                originFile.setFileType("GZ");
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

            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("网络统计数据导入");
            dataJob.setType(vm.getModuleName().toUpperCase());
            dataJob.setOriginFile(originFile);
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));
            dataJob.setArea(area);
            dataJob.setCreatedDate(new Date());
            dataJob.setPriority(2);
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setStatus("等待处理");
            dataJobRepository.save(dataJob);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
