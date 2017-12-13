package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import com.hgicreate.rno.domain.gsm.GsmStructJobParam;
import com.hgicreate.rno.domain.gsm.GsmStructJobReport;
import com.hgicreate.rno.repository.gsm.GsmStructAnalysisJobRepository;
import com.hgicreate.rno.repository.gsm.GsmStructJobParamRepository;
import com.hgicreate.rno.repository.gsm.GsmStructJobReportRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmStructAnalysisService;
import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.service.gsm.dto.StructJobReportDTO;
import com.hgicreate.rno.service.gsm.mapper.StructJobReportMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructTaskInfoVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/gsm-struct-analysis")
public class GsmStructAnalysisResource {
    private final GsmStructAnalysisService gsmStructAnalysisService;
    private final GsmStructAnalysisJobRepository gsmStructAnalysisJobRepository;

    private final GsmStructJobParamRepository gsmStructJobParamRepository;
    private final GsmStructJobReportRepository gsmStructJobReportRepository;

    public GsmStructAnalysisResource(GsmStructAnalysisService gsmStructAnalysisService,
                                     GsmStructAnalysisJobRepository gsmStructAnalysisJobRepository,
                                     GsmStructJobParamRepository gsmStructJobParamRepository,
                                     GsmStructJobReportRepository gsmStructJobReportRepository) {
        this.gsmStructAnalysisService = gsmStructAnalysisService;
        this.gsmStructAnalysisJobRepository = gsmStructAnalysisJobRepository;
        this.gsmStructJobParamRepository = gsmStructJobParamRepository;
        this.gsmStructJobReportRepository = gsmStructJobReportRepository;
    }

    @PostMapping("/task-query")
    public List<GsmStructAnalysisJobDTO> taskQuery(GsmStructAnalysisQueryVM vm) throws ParseException {
        return gsmStructAnalysisService.taskQuery(vm);
    }

    @PostMapping("/query-file-number")
    public List<Map<String,Object>> queryFileNumber(GsmStructTaskInfoVM vm) throws ParseException {
        return gsmStructAnalysisService.queryFileNumber(vm);
    }

    @PostMapping("/query-report")
    public List<StructJobReportDTO> queryReport(String id){
        log.debug("查询任务报告的任务id：{}",id);
        return gsmStructJobReportRepository.findByGsmStructAnalysisJob_Id(Long.parseLong(id))
                .stream().map(StructJobReportMapper.INSTANCE::structJobReportToStructJobReportDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/submit-task")
    public void submitTask(GsmStructTaskInfoVM vm) throws ParseException {
        log.debug("任务信息：{}",vm);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date begMeaDate = sdf.parse(vm.getBegMeaDate());
        Date endMeaDate = sdf.parse(vm.getEndMeaDate());

        Date createdDate = new Date();

        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTime(createdDate);
        startTimeCal.add(Calendar.SECOND,2);
        Date startTime = startTimeCal.getTime();
        startTimeCal.add(Calendar.SECOND,30);
        Date completeTime = startTimeCal.getTime();

        List<Map<String,Object>> list = gsmStructAnalysisService.queryFileNumber(vm);
        int fileNum = 0;
        for(Map<String,Object> map:list){
            if(!("--".equals(map.get("mrrNum")))){
                fileNum = fileNum+Integer.parseInt(map.get("mrrNum").toString());
            }
            if(!("--".equals(map.get("ncsNum")))){
                fileNum = fileNum+Integer.parseInt(map.get("ncsNum").toString());
            }
            if(!("--".equals(map.get("bscNum")))){
                fileNum = fileNum+Integer.parseInt(map.get("bscNum").toString());
            }
        }

        Area area = new Area();
        area.setId(vm.getCityId());

        // 保存任务信息
        GsmStructAnalysisJob gsmStructAnalysisJob = new GsmStructAnalysisJob();
        gsmStructAnalysisJob.setName(vm.getJobName());
        gsmStructAnalysisJob.setArea(area);
        gsmStructAnalysisJob.setDescription(vm.getTaskDescription());
        gsmStructAnalysisJob.setPriority(1);
        gsmStructAnalysisJob.setBegMeaTime(begMeaDate);
        gsmStructAnalysisJob.setEndMeaTime(endMeaDate);
        gsmStructAnalysisJob.setCreatedDate(createdDate);
        gsmStructAnalysisJob.setStartTime(startTime);
        gsmStructAnalysisJob.setCompleteTime(completeTime);
        if(fileNum == 0){
            gsmStructAnalysisJob.setStatus("异常终止");
            gsmStructAnalysisJob.setCompleteTime(addSecond(startTime,1));
        }else {
            gsmStructAnalysisJob.setStatus("正常完成");
        }
        gsmStructAnalysisJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        gsmStructAnalysisJob.setFileNumber(fileNum);
        gsmStructAnalysisJobRepository.save(gsmStructAnalysisJob);

        // 保存任务参数
        List<GsmStructJobParam> gsmStructJobParamList = new ArrayList<>();
        GsmStructJobParam gsmStructJobParam = new GsmStructJobParam();
        gsmStructJobParam.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam.setParamCode("SAMEFREQINTERTHRESHOLD");
        gsmStructJobParam.setParamVal(vm.getSAMEFREQINTERTHRESHOLD());
        GsmStructJobParam gsmStructJobParam1 = new GsmStructJobParam();
        gsmStructJobParam1.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam1.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParamList.add(gsmStructJobParam1);
        GsmStructJobParam gsmStructJobParam2 = new GsmStructJobParam();
        gsmStructJobParam2.setParamCode("OVERSHOOTINGIDEALDISMULTIPLE");
        gsmStructJobParam2.setParamVal(vm.getOVERSHOOTINGIDEALDISMULTIPLE());
        gsmStructJobParam2.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam2.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParamList.add(gsmStructJobParam2);
        GsmStructJobParam gsmStructJobParam3 = new GsmStructJobParam();
        gsmStructJobParam3.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam3.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam3.setParamCode("BETWEENCELLIDEALDISMULTIPLE");
        gsmStructJobParam3.setParamVal(vm.getBETWEENCELLIDEALDISMULTIPLE());
        gsmStructJobParamList.add(gsmStructJobParam3);
        GsmStructJobParam gsmStructJobParam4 = new GsmStructJobParam();
        gsmStructJobParam4.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam4.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam4.setParamCode("CELLCHECKTIMESIDEALDISMULTIPLE");
        gsmStructJobParam4.setParamVal(vm.getCELLCHECKTIMESIDEALDISMULTIPLE());
        gsmStructJobParamList.add(gsmStructJobParam4);
        GsmStructJobParam gsmStructJobParam5 = new GsmStructJobParam();
        gsmStructJobParam5.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam5.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam5.setParamCode("CELLDETECTCITHRESHOLD");
        gsmStructJobParam5.setParamVal(vm.getCELLDETECTCITHRESHOLD());
        gsmStructJobParamList.add(gsmStructJobParam5);
        GsmStructJobParam gsmStructJobParam6 = new GsmStructJobParam();
        gsmStructJobParam6.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam6.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam6.setParamCode("CELLIDEALDISREFERENCECELLNUM");
        gsmStructJobParam6.setParamVal(vm.getCELLIDEALDISREFERENCECELLNUM());
        gsmStructJobParamList.add(gsmStructJobParam6);
        GsmStructJobParam gsmStructJobParam7 = new GsmStructJobParam();
        gsmStructJobParam7.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam7.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam7.setParamCode("GSM900CELLFREQNUM");
        gsmStructJobParam7.setParamVal(vm.getGSM900CELLFREQNUM());
        gsmStructJobParamList.add(gsmStructJobParam7);
        GsmStructJobParam gsmStructJobParam8 = new GsmStructJobParam();
        gsmStructJobParam8.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam8.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam8.setParamCode("GSM1800CELLFREQNUM");
        gsmStructJobParam8.setParamVal(vm.getGSM1800CELLFREQNUM());
        gsmStructJobParamList.add(gsmStructJobParam8);
        GsmStructJobParam gsmStructJobParam9 = new GsmStructJobParam();
        gsmStructJobParam8.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam8.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam8.setParamCode("GSM900CELLIDEALCAPACITY");
        gsmStructJobParam8.setParamVal(vm.getGSM900CELLIDEALCAPACITY());
        gsmStructJobParamList.add(gsmStructJobParam9);
        GsmStructJobParam gsmStructJobParam10 = new GsmStructJobParam();
        gsmStructJobParam10.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam10.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam10.setParamCode("GSM1800CELLIDEALCAPACITY");
        gsmStructJobParam10.setParamVal(vm.getGSM1800CELLIDEALCAPACITY());
        gsmStructJobParamList.add(gsmStructJobParam10);
        GsmStructJobParam gsmStructJobParam11 = new GsmStructJobParam();
        gsmStructJobParam11.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam11.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam11.setParamCode("DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD");
        gsmStructJobParam11.setParamVal(vm.getDLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD());
        gsmStructJobParamList.add(gsmStructJobParam11);
        GsmStructJobParam gsmStructJobParam12 = new GsmStructJobParam();
        gsmStructJobParam12.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam12.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam12.setParamCode("ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD");
        gsmStructJobParam12.setParamVal(vm.getULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD());
        gsmStructJobParamList.add(gsmStructJobParam12);
        GsmStructJobParam gsmStructJobParam13 = new GsmStructJobParam();
        gsmStructJobParam13.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam13.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam13.setParamCode("INTERFACTORMOSTDISTANT");
        gsmStructJobParam13.setParamVal(vm.getINTERFACTORMOSTDISTANT());
        gsmStructJobParamList.add(gsmStructJobParam13);
        GsmStructJobParam gsmStructJobParam14 = new GsmStructJobParam();
        gsmStructJobParam14.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam14.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam14.setParamCode("INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD");
        gsmStructJobParam14.setParamVal(vm.getINTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD());
        gsmStructJobParamList.add(gsmStructJobParam14);
        GsmStructJobParam gsmStructJobParam15 = new GsmStructJobParam();
        gsmStructJobParam15.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam15.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam15.setParamCode("RELATIONNCELLCITHRESHOLD");
        gsmStructJobParam15.setParamVal(vm.getRELATIONNCELLCITHRESHOLD());
        gsmStructJobParamList.add(gsmStructJobParam15);
        GsmStructJobParam gsmStructJobParam16 = new GsmStructJobParam();
        gsmStructJobParam16.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam16.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam16.setParamCode("TOTALSAMPLECNTSMALL");
        gsmStructJobParam16.setParamVal(vm.getTOTALSAMPLECNTSMALL());
        gsmStructJobParamList.add(gsmStructJobParam16);
        GsmStructJobParam gsmStructJobParam17 = new GsmStructJobParam();
        gsmStructJobParam17.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam17.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam17.setParamCode("TOTALSAMPLECNTTOOSMALL");
        gsmStructJobParam17.setParamVal(vm.getTOTALSAMPLECNTTOOSMALL());
        gsmStructJobParamList.add(gsmStructJobParam17);
        GsmStructJobParam gsmStructJobParam18 = new GsmStructJobParam();
        gsmStructJobParam18.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        gsmStructJobParam18.setParamType("STRUCTANA_THRESHOLD");
        gsmStructJobParam18.setParamCode("SAMEFREQINTERCOEFBIG");
        gsmStructJobParam18.setParamVal(vm.getSAMEFREQINTERCOEFBIG());
        gsmStructJobParamList.add(gsmStructJobParam18);
        gsmStructJobParam.setParamCode("SAMEFREQINTERCOEFSMALL");
        gsmStructJobParam.setParamVal(vm.getSAMEFREQINTERCOEFSMALL());
        GsmStructJobParam gsmStructJobParam19 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam19);
        gsmStructJobParam.setParamCode("OVERSHOOTINGCOEFRFFERDISTANT");
        gsmStructJobParam.setParamVal(vm.getOVERSHOOTINGCOEFRFFERDISTANT());
        GsmStructJobParam gsmStructJobParam20 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam20);
        gsmStructJobParam.setParamCode("NONNCELLSAMEFREQINTERCOEF");
        gsmStructJobParam.setParamVal(vm.getNONNCELLSAMEFREQINTERCOEF());
        GsmStructJobParam gsmStructJobParam21 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam21);
        gsmStructJobParam.setParamType("STRUCTANA_DATATYPE");
        gsmStructJobParam.setParamCode("USEERIDATA");
        if(vm.getUseEriData()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        gsmStructJobParam.setParamCode("USEHWDATA");
        if(vm.getUseHwData()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        gsmStructJobParam.setParamType("STRUCTANA_CALPROCE");
        gsmStructJobParam.setParamCode("CALCLUSTERWEIGHT");
        if(vm.getCalClusterWeight()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        GsmStructJobParam gsmStructJobParam22 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam22);
        gsmStructJobParam.setParamCode("CALIDEALDIS");
        if(vm.getCalIdealDis()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        GsmStructJobParam gsmStructJobParam23 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam23);
        gsmStructJobParam.setParamCode("CALCLUSTERCONSTRAIN");
        if(vm.getCalClusterConstrain()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        GsmStructJobParam gsmStructJobParam24 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam24);
        gsmStructJobParam.setParamCode("CALCELLRES");
        if(vm.getCalCellRes()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        GsmStructJobParam gsmStructJobParam25 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam25);
        gsmStructJobParam.setParamCode("CALCONCLUSTER");
        if(vm.getCalConCluster()!=null){
            gsmStructJobParam.setParamVal("Y");
        }else {
            gsmStructJobParam.setParamVal("N");
        }
        GsmStructJobParam gsmStructJobParam26 = gsmStructJobParam;
        gsmStructJobParamList.add(gsmStructJobParam26);
        gsmStructJobParamRepository.save(gsmStructJobParamList);

        // 保存任务报告
        List<GsmStructJobReport> gsmStructJobReportList = new ArrayList<>();
        GsmStructJobReport gsmStructJobReport = new GsmStructJobReport();
        gsmStructJobReport.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        if(fileNum!=0){
            if(vm.getUseEriData()!=null){
                gsmStructJobReport.setStage("完成爱立信NCS数据准备");
                gsmStructJobReport.setStartTime(startTime);
                gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),2));
                gsmStructJobReport.setStatus("成功");
                gsmStructJobReportList.add(gsmStructJobReport);

                gsmStructJobReport.setStage("完成爱立信MRR数据准备");
                gsmStructJobReport.setStartTime(startTimeCal.getTime());
                gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),4));
                gsmStructJobReport.setStatus("成功");
                gsmStructJobReportList.add(gsmStructJobReport);

                if(vm.getUseHwData()!=null){
                    gsmStructJobReport.setStage("完成华为NCS数据准备");
                    gsmStructJobReport.setStartTime(startTimeCal.getTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),3));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);

                    gsmStructJobReport.setStage("完成华为MRR数据准备");
                    gsmStructJobReport.setStartTime(startTimeCal.getTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),5));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);
                }

                if(vm.getCalConCluster()!=null){
                    gsmStructJobReport.setStage("计算最大联通簇");
                    gsmStructJobReport.setStartTime(gsmStructJobReport.getCompleteTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),3));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);
                }
                if(vm.getCalClusterConstrain()!=null){
                    gsmStructJobReport.setStage("计算簇约束因子");
                    gsmStructJobReport.setStartTime(gsmStructJobReport.getCompleteTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),4));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);
                }
                if(vm.getCalClusterWeight()!=null){
                    gsmStructJobReport.setStage("计算簇权重");
                    gsmStructJobReport.setStartTime(gsmStructJobReport.getCompleteTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),3));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);
                }
                if(vm.getCalCellRes()!=null){
                    gsmStructJobReport.setStage("计算结构指数");
                    gsmStructJobReport.setStartTime(gsmStructJobReport.getCompleteTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),2));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);
                }
                if(vm.getCalIdealDis()!=null){
                    gsmStructJobReport.setStage("计算理想距离");
                    gsmStructJobReport.setStartTime(gsmStructJobReport.getCompleteTime());
                    gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),3));
                    gsmStructJobReport.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport);
                }
            }
            gsmStructJobReport.setStage("保存分析结果");
            gsmStructJobReport.setStartTime(gsmStructJobReport.getCompleteTime());
            gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),1));
            gsmStructJobReport.setStatus("成功");
            gsmStructJobReportList.add(gsmStructJobReport);

            gsmStructJobReport.setStage("任务总结");
            gsmStructJobReport.setStartTime(startTime);
            gsmStructJobReport.setCompleteTime(completeTime);
            gsmStructJobReport.setStatus("全部成功");
            gsmStructJobReport.setMessage("结构分析完成！");
            gsmStructJobReportList.add(gsmStructJobReport);
        }else{
            gsmStructJobReport.setStage("任务总结");
            gsmStructJobReport.setStartTime(gsmStructAnalysisJob.getStartTime());
            gsmStructJobReport.setCompleteTime(gsmStructAnalysisJob.getCompleteTime());
            gsmStructJobReport.setStatus("失败");
            gsmStructJobReport.setMessage("结构优化分析异常！没有数据！");
            gsmStructJobReportList.add(gsmStructJobReport);
        }
        gsmStructJobReportRepository.save(gsmStructJobReportList);
    }

    public Date addSecond(Date date,int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }
}
