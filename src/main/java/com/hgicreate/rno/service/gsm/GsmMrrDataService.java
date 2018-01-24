package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.*;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.mapper.gsm.GsmMrrDetailMapper;
import com.hgicreate.rno.repository.DataJobReportRepository;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.OriginFileAttrRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.repository.gsm.GsmMrrDescRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import com.hgicreate.rno.service.gsm.dto.GsmMrrDataQueryDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmMrrDescMapper;
import com.hgicreate.rno.service.mapper.DataJobMapper;
import com.hgicreate.rno.service.mapper.DataJobReportMapper;
import com.hgicreate.rno.util.FtpUtils;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
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
public class GsmMrrDataService {
    private final GsmMrrDescRepository gsmMrrDescRepository;
    private final DataJobRepository dataJobRepository;
    private final GsmMrrDetailMapper gsmMrrDetailMapper;
    private final DataJobReportRepository dataJobReportRepository;
    private  final OriginFileRepository originFileRepository;
    private  final OriginFileAttrRepository originFileAttrRepository;
    private final Environment env;

    public GsmMrrDataService(
            GsmMrrDescRepository gsmMrrDescRepository, DataJobRepository dataJobRepository,
            GsmMrrDetailMapper gsmMrrDetailMapper, DataJobReportRepository dataJobReportRepository,
            OriginFileRepository originFileRepository, OriginFileAttrRepository originFileAttrRepository,
            Environment env) {
        this.gsmMrrDescRepository = gsmMrrDescRepository;
        this.dataJobRepository = dataJobRepository;
        this.gsmMrrDetailMapper = gsmMrrDetailMapper;
        this.dataJobReportRepository = dataJobReportRepository;
        this.originFileRepository = originFileRepository;
        this.originFileAttrRepository = originFileAttrRepository;
        this.env = env;
    }

    /**
     * 查找上传状态历史
     */
    public List<DataJobReportDTO> queryReport(String id){
        log.debug("查询任务报告的任务id：{}",id);
        return dataJobReportRepository.findByDataJob_Id(Long.parseLong(id))
                .stream().map(DataJobReportMapper.INSTANCE::dataJobReportToDataJobReportDTO)
                .collect(Collectors.toList());
    }

    /**
     * MRR数据查询
     */
    public List<GsmMrrDataQueryDTO> mrrDataQuery(GsmMrrDescQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")) {
            return gsmMrrDescRepository.findTop1000ByAreaAndFactoryAndMeaDateBetween(
                    area, vm.getFactory(), vm.getBeginTestDate(), endDate)
                    .stream().map(GsmMrrDescMapper.INSTANCE::gsmMrrDataQueryToDTO)
                    .collect(Collectors.toList());
        }
        return gsmMrrDescRepository.findTop1000ByAreaAndFactoryAndBscLikeAndMeaDateBetween(
                area, vm.getFactory(), "%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate)
                .stream().map(GsmMrrDescMapper.INSTANCE::gsmMrrDataQueryToDTO)
                .collect(Collectors.toList());
    }

    /**
     * gsm文件上传查询
     */
    public List<GsmImportQueryDTO> gsmImportQuery(GsmImportQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
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
     * 查询爱立信Mrr详情信息
     */
    public List<Map<String, Object>> queryEriMrrDetailByPage(
            Long mrrDescId) {
        GsmMrrDesc mrrDescDetails = gsmMrrDescRepository.findOne(mrrDescId);
        long cityId;
        Date meaTime;
        if (mrrDescDetails != null) {
            if (mrrDescDetails.getArea() != null
                    && mrrDescDetails.getMeaDate() != null) {
                cityId = mrrDescDetails.getArea().getId();
                meaTime = mrrDescDetails.getMeaDate();
            } else {
                log.warn("mrrDescId=" + mrrDescId + "的描述信息中，cityId或者meaTime为空");
                return Collections.emptyList();
            }
        } else {
            log.warn("mrrDescId=" + mrrDescId + ",不存在对应描述信息");
            return Collections.emptyList();
        }

        List<Map<String, Object>> resCellAndBsc = gsmMrrDetailMapper
                .queryEriMrrCellAndBscByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resUlQua6t7Rate = gsmMrrDetailMapper
                .queryEriMrrUlQua6t7RateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlQua6t7Rate = gsmMrrDetailMapper
                .queryEriMrrDlQua6t7RateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resUlStrenRate = gsmMrrDetailMapper
                .queryEriMrrUlStrenRateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlStrenRate = gsmMrrDetailMapper
                .queryEriMrrDlStrenRateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlWeekSignal = gsmMrrDetailMapper
                .queryEriMrrDlWeekSignalByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resAverTa = gsmMrrDetailMapper
                .queryEriMrrAverTaByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resMaxTa = gsmMrrDetailMapper
                .queryEriMrrMaxTaByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resUlQua0t5Rate = gsmMrrDetailMapper
                .queryEriMrrUlQua0t5RateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlQua0t5Rate = gsmMrrDetailMapper
                .queryEriMrrDlQua0t5RateByDescId(mrrDescId, cityId, meaTime);

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map;
        String cell;
        if (!(resCellAndBsc.size() == resUlQua6t7Rate.size()
                && resUlQua6t7Rate.size() == resDlQua6t7Rate.size()
                && resDlQua6t7Rate.size() == resUlStrenRate.size()
                && resUlStrenRate.size() == resDlStrenRate.size()
                && resDlStrenRate.size() == resDlWeekSignal.size()
                && resDlWeekSignal.size() == resAverTa.size()
                && resAverTa.size() == resMaxTa.size()
                && resMaxTa.size() == resUlQua0t5Rate.size()
                && resUlQua0t5Rate.size() == resDlQua0t5Rate.size()
        )) {
            return null;
        }
        for (int i = 0; i < resCellAndBsc.size(); i++) {
            map = new HashMap<>();
            cell = resCellAndBsc.get(i).get("CELL_NAME").toString();
            map.put("CELL_NAME", cell);
            map.put("BSC", resCellAndBsc.get(i).get("BSC").toString());
            if (resUlQua6t7Rate.size() == 0 || resUlQua6t7Rate.get(i).get("UL_QUA6T7_RATE") == null) {
                map.put("UL_QUA6T7_RATE", "--");
            } else {
                if (cell.equals(resUlQua6t7Rate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("UL_QUA6T7_RATE", resUlQua6t7Rate.get(i).get("UL_QUA6T7_RATE"));
                } else {
                    map.put("UL_QUA6T7_RATE", "--");
                }
            }
            if (resDlQua6t7Rate.size() == 0 || resDlQua6t7Rate.get(i).get("DL_QUA6T7_RATE") == null) {
                map.put("DL_QUA6T7_RATE", "--");
            } else {
                if (cell.equals(resDlQua6t7Rate.get(i).get("CELL_NAME").toString())) {
                    map.put("DL_QUA6T7_RATE", resDlQua6t7Rate.get(i).get("DL_QUA6T7_RATE"));
                } else {
                    map.put("DL_QUA6T7_RATE", "--");
                }
            }
            if (resUlStrenRate.size() == 0 || resUlStrenRate.get(i).get("UL_STREN_RATE") == null) {
                map.put("UL_STREN_RATE", "--");
            } else {
                if (cell.equals(resUlStrenRate.get(i).get("CELL_NAME").toString())) {
                    map.put("UL_STREN_RATE", resUlStrenRate.get(i).get("UL_STREN_RATE"));
                } else {
                    map.put("UL_STREN_RATE", "--");
                }
            }
            if (resDlStrenRate.size() == 0 || resDlStrenRate.get(i).get("DL_STREN_RATE") == null) {
                map.put("DL_STREN_RATE", "--");
            } else {
                if (cell.equals(resDlStrenRate.get(i).get("CELL_NAME").toString())) {
                    map.put("DL_STREN_RATE", resDlStrenRate.get(i).get("DL_STREN_RATE"));
                } else {
                    map.put("DL_STREN_RATE", "--");
                }
            }
            if (resDlWeekSignal.size() == 0 || resDlWeekSignal.get(i).get("DL_WEEK_SIGNAL") == null) {
                map.put("DL_WEEK_SIGNAL", "--");
            } else {
                if (cell.equals(resDlWeekSignal.get(i).get("CELL_NAME").toString())) {
                    map.put("DL_WEEK_SIGNAL", resDlWeekSignal.get(i).get("DL_WEEK_SIGNAL"));
                } else {
                    map.put("DL_WEEK_SIGNAL", "--");
                }
            }
            if (resAverTa.size() == 0 || resAverTa.get(i).get("AVER_TA") == null) {
                map.put("AVER_TA", "--");
            } else {
                if (cell.equals(resAverTa.get(i).get("CELL_NAME").toString())) {
                    map.put("AVER_TA", resAverTa.get(i).get("AVER_TA"));
                } else {
                    map.put("AVER_TA", "--");
                }
            }
            if (resMaxTa.size() == 0 || resMaxTa.get(i).get("MAX_TA") == null) {
                map.put("MAX_TA", "--");
            } else {
                if (cell.equals(resMaxTa.get(i).get("CELL_NAME").toString())) {
                    map.put("MAX_TA", resMaxTa.get(i).get("MAX_TA"));
                } else {
                    map.put("MAX_TA", "--");
                }
            }
            if (resUlQua0t5Rate.size() == 0 || resUlQua0t5Rate.get(i).get("UL_QUA0T5_RATE") == null) {
                map.put("UL_QUA0T5_RATE", "--");
            } else {
                if (cell.equals(resUlQua0t5Rate.get(i).get("CELL_NAME").toString())) {
                    map.put("UL_QUA0T5_RATE", resUlQua0t5Rate.get(i).get("UL_QUA0T5_RATE"));
                } else {
                    map.put("UL_QUA0T5_RATE", "--");
                }
            }
            if (resDlQua0t5Rate.size() == 0 ||resDlQua0t5Rate.get(i).get("DL_QUA0T5_RATE") == null) {
                map.put("DL_QUA0T5_RATE", "--");
            } else {
                if (cell.equals(resDlQua0t5Rate.get(i).get("CELL_NAME").toString())) {
                    map.put("DL_QUA0T5_RATE", resDlQua0t5Rate.get(i).get("DL_QUA0T5_RATE"));
                } else {
                    map.put("DL_QUA0T5_RATE", "--");
                }
            }
            result.add(map);
        }
        return result;
    }

    /**
     * mrrData上传文件
     */
    public ResponseEntity<?> uploadFile(GsmUploadVM vm) {
        try {
            Date uploadBeginTime = new Date();
            // 获取文件名，并构建为本地文件路径
            String filename = vm.getFile().getOriginalFilename();
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
                filename = UUID.randomUUID().toString() +".csv";
                originFile.setFileType("CSV");
            }else if("application/x-zip-compressed".equals(vm.getFile().getContentType())){
                originFile.setFileType("ZIP");
                filename = UUID.randomUUID().toString() +".zip";
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
            dataJob.setName("MRR数据导入");
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
