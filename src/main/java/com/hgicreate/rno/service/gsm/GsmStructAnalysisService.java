package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.domain.gsm.GsmThreshold;
import com.hgicreate.rno.mapper.gsm.GsmStructAnalysisQueryMapper;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmMrrDescRepository;
import com.hgicreate.rno.repository.gsm.GsmThresholdRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.service.gsm.dto.GsmThresholdDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmThresholdMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructTaskInfoVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GsmStructAnalysisService {
    private final GsmStructAnalysisQueryMapper gsmStructAnalysisQueryMapper;
    private final GsmThresholdRepository gsmThresholdRepository;

    private final GsmMrrDescRepository gsmMrrDescRepository;
    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    public GsmStructAnalysisService(GsmStructAnalysisQueryMapper gsmStructAnalysisQueryMapper,
                                    GsmThresholdRepository gsmThresholdRepository,
                                    GsmMrrDescRepository gsmMrrDescRepository,
                                    GsmEriNcsDescRepository gsmEriNcsDescRepository) {
        this.gsmStructAnalysisQueryMapper = gsmStructAnalysisQueryMapper;
        this.gsmThresholdRepository = gsmThresholdRepository;
        this.gsmMrrDescRepository = gsmMrrDescRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
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

    public List<GsmThresholdDTO> getTaskParams() {
        List<GsmThreshold> list = gsmThresholdRepository.findByModuleType("STRUCTANA");
        return list.stream()
                .map(GsmThresholdMapper.INSTANCE::gsmThresholdToGsmThresholdDTO)
                .collect(Collectors.toList());
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

    private static List<Date> findDates(Date dBegin, Date dEnd) {
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
