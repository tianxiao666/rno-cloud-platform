package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmTrafficQuality;
import com.hgicreate.rno.mapper.gsm.GsmTrafficMapper;
import com.hgicreate.rno.repository.gsm.GsmTrafficQualityRepository;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficQualityQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmTrafficService {
    private final GsmTrafficMapper gsmTrafficMapper;
    private final GsmTrafficQualityRepository gsmTrafficQualityRepository;

    public GsmTrafficService(GsmTrafficMapper gsmTrafficMapper, GsmTrafficQualityRepository gsmTrafficQualityRepository) {
        this.gsmTrafficMapper = gsmTrafficMapper;
        this.gsmTrafficQualityRepository = gsmTrafficQualityRepository;
    }

    public List<GsmTrafficQueryDTO> gsmTrafficQuery(GsmTrafficQueryVM vm){
        return gsmTrafficMapper.gsmTrafficQuery(vm);
    }

    public File downloadData(GsmTrafficQueryVM vm) throws ParseException {
        List<GsmTrafficQueryDTO> list = gsmTrafficQuery(vm);
        ListOrderedMap<String, String> columnTitles = new ListOrderedMap<>();
        columnTitles.put("stsDate", "DATE");
        columnTitles.put("period", "PERIOD");
        columnTitles.put("bsc", "BSC");
        columnTitles.put("cellEnglishName", "CELL");
        columnTitles.put("cellChineseName", "小区");
        columnTitles.put("trate", "T完好率");
        columnTitles.put("defChannel", "定义信道");
        columnTitles.put("availableChannel", "可用信道");
        columnTitles.put("carrierNum", "载波数");
        columnTitles.put("wirelessUseRate", "无线资源利用率");
        columnTitles.put("trafficVolume", "话务量");
        columnTitles.put("preTrafficVolume", "每线话务量");
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File file = new File(vm.getSearchType() + sdf.format(today) + "-话务性能指标.csv");

        List<String> columns = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        for (Object column : columnTitles.keySet()) {
            columns.add((String) column);
            columnNames.add(columnTitles.get(column));
        }
        //获取UTF-8编码文本文件开头的BOM签名
        byte b[] = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        String s = new String(b);
        BufferedWriter bw = null;
        FileWriter fw = null;
        StringBuilder str = new StringBuilder();
        try {
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            str.append(columnNames.get(0));
            for (int i = 1; i < columnNames.size(); i++) {
                str.append(",").append(columnNames.get(i));
            }
            bw.write(s + str.toString());
            bw.newLine();
            for ( GsmTrafficQueryDTO data : list) {
                str = new StringBuilder();
                str.append(s).append(data.getStsDate().toString()).append(",");
                str.append(s).append(data.getPeriod()).append(",");
                str.append(s).append(data.getBsc()).append(",");
                str.append(s).append(data.getCellEnglishName()).append(",");
                str.append(s).append(data.getCellChineseName()).append(",");
                str.append(s).append(data.getTRate().toString()).append(",");
                str.append(s).append(data.getDefChannel().toString()).append(",");
                str.append(s).append(data.getAvailableChannel().toString()).append(",");
                str.append(s).append(data.getCarrierNum().toString()).append(",");
                str.append(s).append(data.getWirelessUseRate().toString()).append(",");
                str.append(s).append(data.getTrafficVolume().toString()).append(",");
                str.append(s).append(data.getPreTrafficVolume().toString()).append(",");
                str.deleteCharAt(str.length() - 1);
                bw.write(str.toString());
                bw.newLine();
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (bw != null) {
                    bw.flush();
                }
                if (fw != null) {
                    fw.flush();
                }
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return file;
    }


    public List<GsmTrafficQuality> gsmTrafficQualityQuery(GsmTrafficQualityQueryVM vm){
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getLatestAllowedTime());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (vm.getType() == null){
            return gsmTrafficQualityRepository.findTop1000ByAreaAndStaticTimeBetween(area, vm.getBeginTime(), endDate);

        }
        return gsmTrafficQualityRepository.findTop1000ByAreaAndTypeAndStaticTimeBetween(area, vm.getType(), vm.getBeginTime(), endDate);
    }
}
