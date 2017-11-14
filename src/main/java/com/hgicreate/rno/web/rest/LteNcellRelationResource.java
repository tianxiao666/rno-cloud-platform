package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.OriginFile;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.NcellRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.LteNcellRelationService;
import com.hgicreate.rno.service.dto.LteNcellImportDtDTO;
import com.hgicreate.rno.service.dto.LteNcellImportFileDTO;
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
import java.text.ParseException;
import java.util.Date;
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
    private final OriginFileRepository originFileRepository;

    private final DataJobRepository dataJobRepository;

    public LteNcellRelationResource(NcellRepository ncellRepository, LteNcellRelationService lteNcellRelationService, OriginFileRepository originFileRepository, DataJobRepository dataJobRepository) {
        this.ncellRepository = ncellRepository;
        this.lteNcellRelationService = lteNcellRelationService;
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
    }

    @PostMapping("/ncell-query")
    public List<LteNcellRelationDTO> ncellQuery(LteNcellRelationQueryVM vm) {
        log.debug("查询LTE邻区关系");
        return lteNcellRelationService.queryNcellRelationDTOs(vm);
    }

    @PostMapping("/ncell-import-query")
    public List<LteNcellImportFileDTO> importQuery(LteNcellImportQueryVM vm) throws ParseException {
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

            //获取文件类型
            String fileExtension = filename.substring(filename.lastIndexOf("."),filename.length()).toLowerCase();
            String fileType = new String("ZIP");
            if((".csv").equals(fileExtension)){
                fileType = "CSV";
            }
            log.debug("上传的文件类型：{}",fileType);

            //获取文件大小
            int fileSize = (int) vm.getFile().getSize();
            log.debug("上传的文件大小：{}",fileSize);

            // 如果目录不存在则创建目录
            File fileDirectory = new File(directory+"/"+vm.getModuleName());
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 以随机的 UUID 为文件名存储在本地
            filename = UUID.randomUUID().toString()+fileExtension;
            String filepath = Paths.get(directory+"/"+vm.getModuleName(), filename).toString();

            log.debug("存储的文件名：{}", filename);

            // 保存文件到本地
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(vm.getFile().getBytes());
            stream.close();

            //创建OriginFile对象，保存文件记录
            OriginFile originFile = new OriginFile();
            originFile.setFilename(vm.getFile().getOriginalFilename());
            originFile.setFileType(fileType);
            originFile.setFileSize(fileSize);
            originFile.setFullPath(filepath);
            originFile.setDataType("LTE-NCELL-RELATION-DATA");
            originFile.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            originFile.setCreatedDate(new Date());
            originFile.setSourceType("上传");
            originFileRepository.save(originFile);

            //创建DataJob对象，创建文件任务
            Area area = new Area();
            area.setId(Long.parseLong(vm.getAreaId()));

            DataJob dataJob = new DataJob();
            dataJob.setName("邻区关系导入");
            dataJob.setType("LTE-NCELL-RELATION-DATA");
            dataJob.setPriority(1);
            dataJob.setArea(area);
            dataJob.setOriginFile(originFile);
            dataJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
            dataJob.setCreatedDate(new Date());
            dataJob.setStatus("等待处理");
            dataJobRepository.save(dataJob);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
