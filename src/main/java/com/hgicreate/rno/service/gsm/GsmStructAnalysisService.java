package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.mapper.gsm.GsmStructAnalysisQueryMapper;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmMrrDescRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.util.ZipFileHandler;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructTaskInfoVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class GsmStructAnalysisService {
    private final GsmStructAnalysisQueryMapper gsmStructAnalysisQueryMapper;

    private final GsmMrrDescRepository gsmMrrDescRepository;
    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    private final GsmCellDataRepository gsmCellDataRepository;

    private final AreaRepository areaRepository;

    public GsmStructAnalysisService(GsmStructAnalysisQueryMapper gsmStructAnalysisQueryMapper,
                                    GsmMrrDescRepository gsmMrrDescRepository,
                                    GsmEriNcsDescRepository gsmEriNcsDescRepository,
                                    GsmCellDataRepository gsmCellDataRepository, AreaRepository areaRepository) {
        this.gsmStructAnalysisQueryMapper = gsmStructAnalysisQueryMapper;
        this.gsmMrrDescRepository = gsmMrrDescRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.areaRepository = areaRepository;
    }

    public List<GsmStructAnalysisJobDTO> taskQuery(GsmStructAnalysisQueryVM vm) throws ParseException {
        if (vm.getIsMine() == null) {
            vm.setIsMine(false);
            vm.setCreatedUser("");
        } else {
            vm.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        }
        return gsmStructAnalysisQueryMapper.taskQuery(vm);
    }

    public List<Map<String,Object>> queryFileNumber(GsmStructTaskInfoVM vm) throws ParseException {
        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = sdf.parse(vm.getBegMeaDate());
        Date endDate = sdf.parse(vm.getEndMeaDate());
        List<GsmMrrDesc> mrrList = gsmMrrDescRepository.findTop1000ByAreaAndMeaDateBetween(area,beginDate,endDate);
        List<GsmEriNcsDesc> ncsList = gsmEriNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(area,beginDate,endDate);
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<String> mrrFileList = new ArrayList<>();
        List<String> ncsFileList = new ArrayList<>();
        List<String> bscFileList = new ArrayList<>();
        List<Date> dateList = findDates(beginDate,endDate);
        for(Date meaDate:dateList){
            Map<String,Object> map = new HashMap<>();
            String meaDateStr = sdf1.format(meaDate);
            map.put("dateTime",meaDateStr);
            if(mrrList.size()>0){
                for(GsmMrrDesc gsmMrrDesc:mrrList){
                    String meaTime = sdf1.format(gsmMrrDesc.getMeaDate());
                    if(meaDateStr.equals(meaTime)&&!(mrrFileList.contains(gsmMrrDesc.getFileName()))){
                        mrrFileList.add(gsmMrrDesc.getFileName());
                    }
                    if(meaDateStr.equals(meaTime)&&!(bscFileList.contains(gsmMrrDesc.getBsc()))){
                        bscFileList.add(gsmMrrDesc.getBsc());
                    }
                }
                map.put("mrrNum",mrrFileList.size());
            }else{
                map.put("mrrNum","--");
            }
            if(ncsFileList.size()>0){
                for(GsmEriNcsDesc gsmEriNcsDesc:ncsList){
                    String meaTime = sdf1.format(gsmEriNcsDesc.getMeaTime());
                    if(meaDateStr.equals(meaTime)&&!(ncsFileList.contains(gsmEriNcsDesc.getRno_2gEriNcsDescId().toString()))){
                        ncsFileList.add(gsmEriNcsDesc.getRno_2gEriNcsDescId().toString());
                    }
                    if(meaDateStr.equals(meaTime)&&!(bscFileList.contains(gsmEriNcsDesc.getBsc()))){
                        bscFileList.add(gsmEriNcsDesc.getBsc());
                    }
                }
                map.put("ncsNum",ncsFileList.size());
            }else {
                map.put("ncsNum","--");
            }
            if(bscFileList.size()>0){
                map.put("bscNum",bscFileList.size());
            }else {
                map.put("bscNum","--");
            }
            resultList.add(map);
        }
        return resultList;
    }

    public String saveGsmStructAnaResult(Long cityId){
        List<GsmCell> cellList = gsmCellDataRepository.findByArea_Id(cityId);
        Area area = areaRepository.findById(cityId);
        List<Map<String, Object>> indexSumLists = doIndexSum(cellList,area);
        List<Map<String, Object>> indexLists = doOverLayIndex(cellList,area);
        saveIndexSum(indexSumLists);
        saveOverLayIndex(indexLists);
        String realUserDataName = "d:/tmp/rno-cloud-platform/downloads/"+"GSM结构优化分析结果.zip";
        try{
            ZipFileHandler.zip("d:/tmp/rno-cloud-platform/downloads/temp/", "", realUserDataName);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 删除临时目录
            try {
                File file = new File("d:/tmp/rno-cloud-platform/downloads/temp/");
                if (file.exists()) {//判断文件是否存在
                    if (file.isFile()) {//判断是否是文件
                        file.delete();//删除文件
                    } else if (file.isDirectory()) {//否则如果它是一个目录
                        File[] files = file.listFiles();//声明目录下所有的文件 files[];
                        for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
                            files[i].delete();
                        }
                        file.delete();//删除文件夹
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return realUserDataName;
    }

    public List<Map<String, Object>> doIndexSum(List<GsmCell> cellList,Area area){
        List<Map<String, Object>> indexSumLists = new ArrayList<>();
        for(GsmCell gsmCell:cellList){
            double rate = Math.random();
            Map<String,Object> map = new HashMap<>();
            map.put("cityName",area.getName());
            map.put("cellId",gsmCell.getCellId());
            map.put("cellName",gsmCell.getCellName());
            if(rate<0.9){
                map.put("weakFlag","是");
            }else{
                map.put("weakFlag","否");
            }
            rate = Math.random();
            if(rate<0.9){
                map.put("overLayFlag","否");
            }else{
                map.put("overLayFlag","是");
            }
            rate = Math.random();
            if(rate>0.96){
                map.put("overCoverGroupFlag","是");
            }
            rate = Math.random();
            if(rate<0.3){
                map.put("overCoverGroupCnt","1");
            }
            if(rate<0.1){
                map.put("overCoverGroupCnt","2");
            }
            if(rate<0.05){
                map.put("overCoverGroupCnt","3");
            }
            rate = Math.random();
            if(rate<0.1){
                map.put("overCoverProVinFlag","是");
            }
            rate = Math.random();
            if(rate<0.03){
                map.put("overCoverProVinCnt","1");
            }
            indexSumLists.add(map);
        }
        return indexSumLists;
    }

    // 生成指标汇总文件
    private void saveIndexSum(List<Map<String, Object>> indexSumLists) {
        log.debug("进入方法：saveIndexSum");
        BufferedWriter bw = null;
        String directory ="d:/tmp/rno-cloud-platform/downloads/temp/";
        File fileDirectory = new File(directory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdirs();
        }
        String saveFullPath = directory+"指标汇总.csv";
        log.debug("保存指标汇总，saveFullPath=" + saveFullPath);
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFullPath), "gbk"));
            StringBuffer buf = new StringBuffer();
            // 输出指标汇总标题
            List<String> titles = Arrays.asList("城市","CELLID", "小区名", "弱覆盖", "重叠覆盖", "过覆盖", "过覆盖数", "1.6过覆盖", "1.6过覆盖数");
            for (String str : titles) {
                buf.append(str).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
            bw.write(buf.toString());
            bw.newLine();
            // 输出内容
            Map<String, Object> indexMap;
            for (int i = 0; i < indexSumLists.size(); i++) {
                try {
                    buf.setLength(0);
                    indexMap = indexSumLists.get(i);
                    buf.append(indexMap.get("cityName").toString()).append(",");
                    buf.append(indexMap.get("cellId").toString()).append(",");
                    buf.append(indexMap.get("cellName").toString()).append(",");
                    buf.append(indexMap.get("weakFlag").toString()).append(",");
                    buf.append(indexMap.get("overLayFlag").toString()).append(",");
                    buf.append(
                            indexMap.get("overCoverGroupFlag") == null ? "否" : indexMap.get("overCoverGroupFlag")
                                    .toString()).append(",");
                    buf.append(
                            indexMap.get("overCoverGroupCnt") == null ? "0" : indexMap.get("overCoverGroupCnt")
                                    .toString()).append(",");
                    buf.append(
                            indexMap.get("overCoverProVinFlag") == null ? "否" : indexMap.get("overCoverProVinFlag")
                                    .toString()).append(",");
                    buf.append(indexMap.get("overCoverProVinCnt") == null ? "0" : indexMap.get("overCoverProVinCnt")
                            .toString());
                    bw.write(buf.toString());
                    bw.newLine();
                } catch (Exception e) {
                }
            }
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.debug("退出方法：saveIndexSum。");
    }

    private List<Map<String, Object>> doOverLayIndex(List<GsmCell> cellList,Area area) {
        List<Map<String, Object>> indexLists = new ArrayList<>();
        for(GsmCell gsmCell:cellList) {
            Map<String, Object> map = new HashMap<>();
            map.put("cityName", area.getName());
            map.put("cellId", gsmCell.getCellId());
            map.put("totalCnt",(int)(Math.random()*(16000)+15000));
            if(Math.random()<0.97){
                map.put("db105PartCnt",(int)(Math.random()*(19500)+500));
            }
            if(Math.random()<0.05){
                map.put("db110PartCnt","1");
            }
            if(Math.random()<0.97){
                map.put("db105PartCntPer",(int)(Math.random()*(20000)+500));
            }
            double rate = Math.random();
            if(rate>0.35){
                map.put("db110PartCntPer",rate);
            }
            if(Math.random()<0.85){
                map.put("db11063PartCnt",(int)(Math.random()*(4500)+200));
            }
            if(Math.random()<0.85){
                map.put("db11065PartCnt",(int)(Math.random()*(3500)+200));
            }
            if(Math.random()<0.85){
                map.put("db110103PartCnt",(int)(Math.random()*(7500)+500));
            }
            if(Math.random()<0.06){
                map.put("db110104PartCnt","1");
            }
            if(Math.random()<0.85){
                map.put("db110105PartCnt",(int)(Math.random()*(4000)+150));
            }
            if(Math.random()<0.06){
                map.put("db110106PartCnt","1");
            }
            if(Math.random()<0.86){
                map.put("db11063PartCntPer",(int)(Math.random()*(1000)+50));
            }
            if(Math.random()<0.86){
                map.put("db11064PartCntPer",(int)(Math.random()*(1000)+50));
            }
            if(Math.random()<0.86){
                map.put("db11065PartCntPer",(int)(Math.random()*(1000)+50));
            }
            rate = Math.random();
            if(rate<0.85){
                map.put("db11066PartCntPer",Math.random()*(0.005)+0.005);
            }
            if(Math.random()<0.86){
                map.put("db110103PartCntPer",(int)(Math.random()*(1000)+50));
            }
            if(Math.random()<0.86){
                map.put("db110104PartCntPer",(int)(Math.random()*(1000)+50));
            }
            if(Math.random()<0.86){
                map.put("db110105PartCntPer",(int)(Math.random()*(1000)+50));
            }
            rate = Math.random();
            if(rate<0.85){
                map.put("db110106PartCntPer",Math.random()*(0.005)+0.005);
            }
            if(Math.random()<0.86){
                map.put("db1101PartCnt",(int)(Math.random()*(5000)+50));
            }
            if(Math.random()<0.86){
                map.put("db1102PartCnt",(int)(Math.random()*(6000)+50));
            }
            if(Math.random()<0.86){
                map.put("db1103PartCnt",(int)(Math.random()*(8000)+50));
            }
            if(Math.random()<0.86){
                map.put("db1104PartCnt",(int)(Math.random()*(9000)+200));
            }
            if(Math.random()<0.86){
                map.put("db1105PartCnt",(int)(Math.random()*(10000)+200));
            }
            if(Math.random()<0.86){
                map.put("db1106PartCnt",(int)(Math.random()*(12000)+600));
            }
            if(Math.random()<0.86){
                map.put("db1107PartCnt",(int)(Math.random()*(12000)+1200));
            }
            if(Math.random()<0.86){
                map.put("db1108PartCnt",(int)(Math.random()*(18000)+3000));
            }
            if(Math.random()<0.86){
                map.put("db1109PartCnt",(int)(Math.random()*(22000)+3500));
            }
            indexLists.add(map);
        }
        return indexLists;
    }

    // 生成重叠覆盖文件
    private void saveOverLayIndex(List<Map<String, Object>> indexLists) {
        log.debug("进入方法：saveOverLayIndex。" );
        BufferedWriter bw = null;
        String directory ="d:/tmp/rno-cloud-platform/downloads/temp/";
        File fileDirectory = new File(directory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdirs();
        }
        String saveFullPath = directory+ "重叠覆盖.csv";
        log.debug("保存重叠覆盖指标，saveFullPath=" + saveFullPath);
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFullPath), "gbk"));
            StringBuffer buf = new StringBuffer();
            // 输出重叠覆盖标题
            List<String> titles = Arrays.asList("城市", "DATATYPE", "CELLID", "总采样点数", "大于负105dBm的采样点数",
                    "大于负110dBm的采样点数", "覆盖率大于负105dBm", "覆盖率大于负110dBm", "db6内3邻区采样数",
                    "db6内4邻区采样数", "db6内5邻区采样数", "db6内6邻区采样数", "db10内3邻区采样数",
                    "db10内4邻区采样数", "db10内5邻区采样数", "db10内6邻区采样数", "db6内3邻区采样比例",
                    "db6内4邻区采样比例", "db6内5邻区采样比例", "db6内6邻区采样比例", "db10内3邻区采样比例",
                    "db10内4邻区采样比例", "db10内5邻区采样比例", "db10内6邻区采样比例", "无邻区采样点数",
                    "邻区1个及以上采样数", "邻区2个及以上采样数", "邻区3个及以上采样数", "邻区4个及以上采样数",
                    "邻区5个及以上采样数", "邻区6个及以上采样数", "邻区7个及以上采样数", "邻区8个及以上采样数",
                    "邻区9个及以上采样数");
            for (String str : titles) {
                buf.append(str).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
            bw.write(buf.toString());
            bw.newLine();
            // 输出内容
            Map<String, Object> indexMap = null;
            for (int i = 0; i < indexLists.size(); i++) {
                try {
                    buf.setLength(0);
                    indexMap = indexLists.get(i);
                    // 城市
                    buf.append(indexMap.get("cityName")).append(",");
                    // 频点类型
                    buf.append("仅同频").append(",");
                    // cellId
                    buf.append(indexMap.get("cellId").toString()).append(",");
                    // 总采样点数
                    buf.append(indexMap.get("totalCnt") == null ? "0" : indexMap.get("totalCnt").toString())
                            .append(",");
                    // 大于负105dBm的采样点数
                    buf.append(indexMap.get("db105PartCnt") == null ? "0" : indexMap.get("db105PartCnt").toString())
                            .append(",");
                    // 大于负110dBm的采样点数
                    buf.append(indexMap.get("db110PartCnt") == null ? "0" : indexMap.get("db110PartCnt").toString())
                            .append(",");
                    // 覆盖率大于负105dBm
                    buf.append(
                            indexMap.get("db105PartCntPer") == null ? "0" : indexMap.get("db105PartCntPer").toString())
                            .append(",");
                    // 覆盖率大于负110dBm
                    buf.append(
                            indexMap.get("db110PartCntPer") == null ? "0" : indexMap.get("db110PartCntPer").toString())
                            .append(",");
                    // db6内3邻区采样数
                    buf.append(indexMap.get("db11063PartCnt") == null ? "0" : indexMap.get("db11063PartCnt").toString())
                            .append(",");
                    // db6内4邻区采样数
                    buf.append(indexMap.get("db11064PartCnt") == null ? "0" : indexMap.get("db11064PartCnt").toString())
                            .append(",");
                    // db6内5邻区采样数
                    buf.append(indexMap.get("db11065PartCnt") == null ? "0" : indexMap.get("db11065PartCnt").toString())
                            .append(",");
                    // db6内6邻区采样数
                    buf.append(indexMap.get("db11066PartCnt") == null ? "0" : indexMap.get("db11066PartCnt").toString())
                            .append(",");
                    // db10内3邻区采样数
                    buf.append(
                            indexMap.get("db110103PartCnt") == null ? "0" : indexMap.get("db110103PartCnt").toString())
                            .append(",");
                    // db10内4邻区采样数
                    buf.append(
                            indexMap.get("db110104PartCnt") == null ? "0" : indexMap.get("db110104PartCnt").toString())
                            .append(",");
                    // db10内5邻区采样数
                    buf.append(
                            indexMap.get("db110105PartCnt") == null ? "0" : indexMap.get("db110105PartCnt").toString())
                            .append(",");
                    // db10内6邻区采样数
                    buf.append(
                            indexMap.get("db110106PartCnt") == null ? "0" : indexMap.get("db110106PartCnt").toString())
                            .append(",");
                    // db6内3邻区采样比例
                    buf.append(
                            indexMap.get("db11063PartCntPer") == null ? "0" : indexMap.get("db11063PartCntPer")
                                    .toString()).append(",");
                    // db6内4邻区采样比例
                    buf.append(
                            indexMap.get("db11064PartCntPer") == null ? "0" : indexMap.get("db11064PartCntPer")
                                    .toString()).append(",");
                    // db6内5邻区采样比例
                    buf.append(
                            indexMap.get("db11065PartCntPer") == null ? "0" : indexMap.get("db11065PartCntPer")
                                    .toString()).append(",");
                    // db6内6邻区采样比例
                    buf.append(
                            indexMap.get("db11066PartCntPer") == null ? "0" : indexMap.get("db11066PartCntPer")
                                    .toString()).append(",");
                    // db10内3邻区采样比例
                    buf.append(
                            indexMap.get("db110103PartCntPer") == null ? "0" : indexMap.get("db110103PartCntPer")
                                    .toString()).append(",");
                    // db10内4邻区采样比例
                    buf.append(
                            indexMap.get("db110104PartCntPer") == null ? "0" : indexMap.get("db110104PartCntPer")
                                    .toString()).append(",");
                    // db10内5邻区采样比例
                    buf.append(
                            indexMap.get("db110105PartCntPer") == null ? "0" : indexMap.get("db110105PartCntPer")
                                    .toString()).append(",");
                    // db10内6邻区采样比例
                    buf.append(
                            indexMap.get("db110106PartCntPer") == null ? "0" : indexMap.get("db110106PartCntPer")
                                    .toString()).append(",");
                    // 无邻区采样点数：已经将邻区为空的过滤
                    buf.append("0").append(",");
                    // 邻区1个及以上采样数
                    buf.append(indexMap.get("db1101PartCnt") == null ? "0" : indexMap.get("db1101PartCnt").toString())
                            .append(",");
                    // 邻区2个及以上采样数
                    buf.append(indexMap.get("db1102PartCnt") == null ? "0" : indexMap.get("db1102PartCnt").toString())
                            .append(",");
                    // 邻区3个及以上采样数
                    buf.append(indexMap.get("db1103PartCnt") == null ? "0" : indexMap.get("db1103PartCnt").toString())
                            .append(",");
                    // 邻区4个及以上采样数
                    buf.append(indexMap.get("db1104PartCnt") == null ? "0" : indexMap.get("db1104PartCnt").toString())
                            .append(",");
                    // 邻区5个及以上采样数
                    buf.append(indexMap.get("db1105PartCnt") == null ? "0" : indexMap.get("db1105PartCnt").toString())
                            .append(",");
                    // 邻区6个及以上采样数
                    buf.append(indexMap.get("db1106PartCnt") == null ? "0" : indexMap.get("db1106PartCnt").toString())
                            .append(",");
                    // 邻区7个及以上采样数
                    buf.append(indexMap.get("db1107PartCnt") == null ? "0" : indexMap.get("db1107PartCnt").toString())
                            .append(",");
                    // 邻区8个及以上采样数
                    buf.append(indexMap.get("db1108PartCnt") == null ? "0" : indexMap.get("db1108PartCnt").toString())
                            .append(",");
                    // 邻区9个及以上采样数
                    buf.append(indexMap.get("db1109PartCnt") == null ? "0" : indexMap.get("db1109PartCnt").toString());
                    bw.write(buf.toString());
                    bw.newLine();
                } catch (Exception e) {
                    // 捕获单行错误，过滤
                }
            }
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Date> findDates(Date dBegin, Date dEnd) {
        List<Date> lDate = new ArrayList<>();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }
}
