package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.domain.gsm.RnoGsmNcell;
import com.hgicreate.rno.mapper.gsm.GsmCoBsicMapper;
import com.hgicreate.rno.repository.*;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmCoBsicService;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsDTO;
import com.hgicreate.rno.service.gsm.dto.CobsicCellsExpandDTO;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicFileUploadVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicSchemaQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/gsm-co-bsic-analysis")
public class GsmCoBsicResource {

    private final GsmCoBsicMapper gsmCoBsicMapper;

    private final GsmCoBsicService gsmCoBsicService;

    private final OriginFileRepository originFileRepository;

    private final OriginFileAttrRepository originFileAttrRepository;

    private final DataJobRepository dataJobRepository;

    private final DataJobReportRepository dataJobReportRepository;

    private final AreaRepository areaRepository;

    private final Environment env;

    public GsmCoBsicResource(GsmCoBsicMapper gsmCoBsicMapper, GsmCoBsicService gsmCoBsicService, OriginFileRepository originFileRepository, OriginFileAttrRepository originFileAttrRepository, DataJobRepository dataJobRepository, DataJobReportRepository dataJobReportRepository, AreaRepository areaRepository, Environment env) {
        this.gsmCoBsicMapper = gsmCoBsicMapper;
        this.gsmCoBsicService = gsmCoBsicService;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.dataJobRepository = dataJobRepository;
        this.dataJobReportRepository = dataJobReportRepository;
        this.areaRepository = areaRepository;
        this.env = env;
    }

    @GetMapping("/whole-net-cobsic-query")
    public Map<String, Object> getWholeNetCoBsicCells(GsmCoBsicQueryVM vm){
        List<CobsicCellsDTO> cobsicCells = gsmCoBsicService.getCobsicCells(vm);
        return getCoBsic(cobsicCells);
    }

    private void prepareCellsExpand(List<RnoGsmNcell> list, CobsicCellsExpandDTO gsmCobsicCellsExpand){
        for (int l = 0; l < list.size(); l++) {
            log.info("获取共同邻区共多少：" + list.size());
            gsmCobsicCellsExpand.setWhetherComNcell(true);
            gsmCobsicCellsExpand.setCommonNcell(list.get(l).getCell());
        }
    }

    @GetMapping("/cobsic-query-by-bcch-bsic")
    public Map<String, Object> getCoBsicCellsByBcchAndBsic(GsmCoBsicQueryVM vm){
        List<CobsicCellsDTO> cobsicCells = gsmCoBsicService.getCobsicCellsByBcchAndBsic(vm);
        return getCoBsic(cobsicCells);
    }

    private Map<String, Object> getCoBsic(List<CobsicCellsDTO> cobsicCells){
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> cobsicMap = new HashMap<>();

        List<String> cellStrs;
        CobsicCellsDTO gsmCobsicCells;
        String bcch;
        String bsic;
        if(cobsicCells != null && cobsicCells.size() != 0){
            for(CobsicCellsDTO one : cobsicCells){
                cellStrs = one.getCells();
                bcch = one.getBcch() + "";
                bsic = one.getBsic() + "";
                //集合转成数组
                String [] labels = cellStrs.toArray(new String [cellStrs.size()]);
                for(int i = 0 ; i < labels.length - 1; i++){
                    //循环判断小区两两之间是否为邻区，或为某小区共同邻区 且距离小于2000米
                    for(int j = i+1; j< labels.length; j++){
                        boolean isNcell = gsmCoBsicMapper.queryNcell(labels[i], labels[j]) != null
                                && gsmCoBsicMapper.queryNcell(labels[i], labels[j]).size() != 0;
                        List<RnoGsmNcell> ncells= gsmCoBsicMapper.queryCommonNcellByTwoCell(labels[i],labels[j]);
                        double distance = gsmCoBsicService.getDistanceBetweenCells(labels[i],labels[j]);
                        if((isNcell|| (ncells !=null && ncells.size() !=0))&& distance < 2000){
                            log.debug("isNcell:" + isNcell);
                            if (cobsicMap.containsKey(bcch + "," + bsic)) {
                                // 通过bcch,bsic为key从map中获取已存在的对象集合
                                gsmCobsicCells = (CobsicCellsDTO) cobsicMap.get(bcch + "," + bsic);
                                // 获取cobsic拓展的组合对象集合
                                List<CobsicCellsExpandDTO> cobsicexpanList =gsmCobsicCells.getCombinedCells();
                                log.info("cobsicCells.getCells():" + cobsicexpanList);
                                // 新建cobsic拓展对象
                                CobsicCellsExpandDTO cellsExpand = new CobsicCellsExpandDTO();
                                cellsExpand.setCombinedCell(labels[i] + "," + labels[j]);
                                cellsExpand.setWhetherNcell(isNcell);
                                cellsExpand.setWhetherComNcell(false);
                                prepareCellsExpand(ncells,cellsExpand);
                                // 为bcch,bsic的所在拓展的对象集合内新增对象
                                cobsicexpanList.add(cellsExpand);
                                gsmCobsicCells.setCombinedCells(cobsicexpanList);
                                cobsicMap.put(bcch + "," + bsic, gsmCobsicCells);
                            } else {
                                gsmCobsicCells = new CobsicCellsDTO();
                                CobsicCellsExpandDTO cellsExpand = new CobsicCellsExpandDTO();
                                cellsExpand.setCombinedCell(labels[i] + "," + labels[j]);
                                cellsExpand.setWhetherNcell(isNcell);
                                prepareCellsExpand(ncells,cellsExpand);
                                gsmCobsicCells.setBcch(Long.parseLong(bcch));
                                gsmCobsicCells.setBsic(bsic);
                                List<CobsicCellsExpandDTO> list = new ArrayList<>();
                                // 向拓展cobsic集合中注入数据
                                list.add(cellsExpand);
                                // 设置cobsic集合对象
                                gsmCobsicCells.setCombinedCells(list);
                                // 通过bcch,bsic为key向map中增加cobsic对象集合
                                cobsicMap.put(bcch + "," + bsic, gsmCobsicCells);
                            }

                        }

                    }
                }
                log.debug("cobsic map = {}",cobsicMap);
            }
            if( !cobsicMap.isEmpty()){
                return cobsicMap;
            }
        }else{
            result.put("fail","不存在co-bsic小区！");
            return result;
        }
        result.put("fail","不存在co-bsic小区！");
        return result;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadGsmCoBsicFile(GsmCoBsicFileUploadVM vm) {
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

            OriginFileAttr originFileAttr1 = new OriginFileAttr();
            originFileAttr1.setOriginFile(originFile);
            originFileAttr1.setName("netType");
            originFileAttr1.setValue(vm.getFileCode());
            originFileAttrRepository.save(originFileAttr1);

            // 保存文件到FTP
            String ftpFullPath = FtpUtils.sendToFtp(vm.getModuleName(), filepath, true, env);
            log.debug("获取FTP文件的全路径：{}", ftpFullPath);

            //建立任务
            DataJob dataJob = new DataJob();
            dataJob.setName("CO-BSIC小区配置数据导入");
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

    @GetMapping("/config-schema-query")
    public List<Map<String,Object>> queryCoBsicConfigSchema(GsmCoBsicSchemaQueryVM vm){
        String cityId = vm.getCityId();
        Area area=areaRepository.findById(Long.parseLong(cityId));
        int gsmCityId=gsmCoBsicMapper.findGsmAreaIdByName(area.getName());
        String schemaName = vm.getSchemaName();
        if(schemaName==null||"".equals(schemaName.trim())  ){
            schemaName =null;
        }else{
            schemaName ="%" + schemaName +"%";
        }
        return gsmCoBsicMapper.queryCoBsicConfigSchema(gsmCityId,schemaName);
    }
}
