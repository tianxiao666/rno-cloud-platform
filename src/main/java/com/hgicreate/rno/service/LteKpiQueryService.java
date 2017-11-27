package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.LteTrafficData;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.LteTrafficDataRepository;
import com.hgicreate.rno.web.rest.vm.LteKpiQueryVM;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LteKpiQueryService {
    private final LteTrafficDataRepository lteTrafficDataRepository;
    private final AreaRepository areaRepository;

    public LteKpiQueryService(LteTrafficDataRepository lteTrafficDataRepository, AreaRepository areaRepository) {
        this.lteTrafficDataRepository = lteTrafficDataRepository;
        this.areaRepository = areaRepository;
    }

    public List<Map<String, Object>> queryResult(LteKpiQueryVM vm) throws ParseException {
        Long areaId = Long.parseLong(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegTime());
        Date endDate = sdf.parse(vm.getEndTime());
        String[] cellNameArr = vm.getCellNameStr().split(",");
        List<LteTrafficData> rows = lteTrafficDataRepository
                .findTop1000ByLteTrafficDesc_AreaIdAndLteTrafficDesc_BeginTimeAfterAndLteTrafficDesc_EndTimeBeforeAndPmUserLabelIn(
                        areaId, beginDate, endDate, cellNameArr);
        List<Map<String, Object>> list = new ArrayList<>();
        if (rows != null && rows.size() > 0) {
            for (LteTrafficData m : rows) {
                Map<String, Object> m1 = getLteStsIndex(m, vm.getIndexColumnStr());
                m1.put("meabegTime", sdf.format(m.getLteTrafficDesc().getBeginTime()));
                m1.put("meaendTime", sdf.format(m.getLteTrafficDesc().getEndTime()));
                m1.put("cellName", m.getPmUserLabel());
                list.add(m1);
            }
        }
        return list;
    }

    private List<Map<String, Object>> queryResultForDownload(LteKpiQueryVM vm) throws ParseException {
        Long areaId = Long.parseLong(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegTime());
        Date endDate = sdf.parse(vm.getEndTime());
        String[] cellNameArr = vm.getCellNameStr().split(",");
        List<LteTrafficData> rows = lteTrafficDataRepository
                .findByLteTrafficDesc_AreaIdAndLteTrafficDesc_BeginTimeAfterAndLteTrafficDesc_EndTimeBeforeAndPmUserLabelIn(
                        areaId, beginDate, endDate, cellNameArr);
        List<Map<String, Object>> list = new ArrayList<>();
        if (rows != null && rows.size() > 0) {
            for (LteTrafficData trafficData : rows) {
                Map<String, Object> map = getLteStsIndex(trafficData, vm.getIndexColumnStr());
                map.put("meabegTime", sdf.format(trafficData.getLteTrafficDesc().getBeginTime()));
                map.put("meaendTime", sdf.format(trafficData.getLteTrafficDesc().getEndTime()));
                map.put("cellName", trafficData.getPmUserLabel());
                list.add(map);
            }
        }
        return list;
    }

    public void downloadData(LteKpiQueryVM vm, HttpServletResponse response) throws ParseException {
        List<Map<String, Object>> map = queryResultForDownload(vm);
        ListOrderedMap<String, String> columnTitles = getColumnTitles(vm.getIndexColumnStr(), vm.getIndexColumnNameStr());

        String areaName = areaRepository.findById(Long.parseLong(vm.getCityId())).getName();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File file = new File(areaName + sdf.format(today) + "-话务性能指标.xlsx");

        String fileName = "";
        try {
            fileName = new String(file.getName().getBytes(), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/x.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        OutputStream os = null;
        try {
            os = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> columns = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        for (Object column : columnTitles.keySet()) {
            columns.add((String) column);
            columnNames.add(columnTitles.get(column));
        }
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row;
        Cell cell;
        row = sheet.createRow((short) 0);
        for (int i = 0; i < columnNames.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(columnNames.get(i));
        }

        for (int i = 0; i < map.size(); i++) {
            row = sheet.createRow( i + 1);
            for (int j = 0; j < columns.size(); j++) {
                if (map.get(i).get(columns.get(j)) instanceof String) {
                    cell = row.createCell(j);
                    cell.setCellValue(map.get(i).get(columns.get(j)).toString());
                } else if (map.get(i).get(columns.get(j)) instanceof Float) {
                    cell = row.createCell(j);
                    cell.setCellValue(Float.parseFloat(map.get(i).get(columns.get(j)).toString()));
                } else {
                    cell = row.createCell(j);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    System.out.println(map.get(i).get(columns.get(j)));
                    cell.setCellValue(sdf1.format((Date) map.get(i).get(columns.get(j))));
                }
            }
        }
        //最终写入文件
        try {
            workbook.write(os);

            if (os != null) {
                os.flush();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ListOrderedMap<String, String> getColumnTitles(String columnList, String columnNameList) {
        String[] columnArr = columnList.split(",");
        String[] columnNameArr = columnNameList.split(",");
        ListOrderedMap<String, String> columnMap = new ListOrderedMap<>();
        columnMap.put("meabegTime", "起始时间");
        columnMap.put("meaendTime", "结束时间");
        columnMap.put("cellName", "小区名称");
        if (columnArr.length == columnNameArr.length) {
            for (int i = 0; i < columnArr.length; i++) {
                columnMap.put(columnArr[i], columnNameArr[i]);
            }
        }
        return columnMap;
    }

    /**
     * 计算指标
     */
    private Map<String, Object> getLteStsIndex(LteTrafficData trafficData, String columnList) {
        return calLteStsIndex(trafficData, columnList);
    }

    private Map<String, Object> calLteStsIndex(LteTrafficData trafficData, String columnList) {

        String[] columnArr = {};
        if (columnList != null && columnList.length() > 0) {
            columnArr = columnList.split(",");
        }
        Map<String, Object> resMap = new HashMap<>();
        for (String colunmName : columnArr) {
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnEstabSucc")) {
                float rrc_ConnEstabSucc;
                if (Float.valueOf(trafficData.getRrcAttconnestab()) == 0) {
                    rrc_ConnEstabSucc = 100;
                } else {
                    rrc_ConnEstabSucc = 100 * Float.valueOf(trafficData.getRrcSuccconnestab()) / Float.valueOf(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(rrc_ConnEstabSucc));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_EstabSucc")) {
                float erab_EstabSucc;
                if (Float.valueOf(trafficData.getErabNbrattestab()) == 0) {
                    erab_EstabSucc = 100;
                } else {
                    erab_EstabSucc = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab());
                }
                resMap.put(colunmName, formatData(erab_EstabSucc));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConn")) {
                float wireConn;
                if ((trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConn = 100;
                } else {
                    wireConn = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConn));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_Drop_CellLevel")) {

                float erab_Drop_CellLevel;
                if ((trafficData.getErabNbrsuccestab()) == 0) {
                    erab_Drop_CellLevel = 0;
                } else {
                    erab_Drop_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail()))
                            / ((trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
                }
                resMap.put(colunmName, formatData(erab_Drop_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnRebuild")) {
                float rrc_ConnRebuild;
                if (((trafficData.getRrcAttconnreestab()) + (trafficData.getRrcAttconnestab())) == 0) {
                    rrc_ConnRebuild = 0;
                } else {
                    rrc_ConnRebuild = 100 * (trafficData.getRrcAttconnreestab()) / ((trafficData.getRrcAttconnreestab()) + (trafficData.getRrcAttconnestab()));
                }
                resMap.put(colunmName, formatData(rrc_ConnRebuild));
            }
            if (colunmName.trim().equalsIgnoreCase("switchSucc")) {
                float switchSucc;
                if (((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb())) == 0) {
                    switchSucc = 100;
                } else {
                    switchSucc = 100 * ((trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb()))
                            / ((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
                }
                resMap.put(colunmName, formatData(switchSucc));
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytes")) {
                float emUplinkSerBytes;
                emUplinkSerBytes = (trafficData.getPdcpUpoctul());
                resMap.put(colunmName, emUplinkSerBytes);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytes")) {
                float emDownlinkSerBytes;
                emDownlinkSerBytes = (trafficData.getPdcpUpoctdl());
                resMap.put(colunmName, emDownlinkSerBytes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI1")) {
                float erab_ConnSuccQCI1;
                if ((trafficData.getErabNbrattestab_1()) == 0) {
                    erab_ConnSuccQCI1 = 100;
                } else {
                    erab_ConnSuccQCI1 = 100 * (trafficData.getErabNbrsuccestab_1()) / (trafficData.getErabNbrattestab_1());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI1));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI2")) {

                float erab_ConnSuccQCI2;
                if ((trafficData.getErabNbrattestab_2()) == 0) {
                    erab_ConnSuccQCI2 = 100;
                } else {
                    erab_ConnSuccQCI2 = 100 * (trafficData.getErabNbrsuccestab_2()) / (trafficData.getErabNbrattestab_2());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI2));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI3")) {
                float erab_ConnSuccQCI3;
                if ((trafficData.getErabNbrattestab_3()) == 0) {
                    erab_ConnSuccQCI3 = 100;
                } else {
                    erab_ConnSuccQCI3 = 100 * (trafficData.getErabNbrsuccestab_3()) / (trafficData.getErabNbrattestab_3());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI3));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI4")) {
                float erab_ConnSuccQCI4;
                if ((trafficData.getErabNbrattestab_4()) == 0) {
                    erab_ConnSuccQCI4 = 100;
                } else {
                    erab_ConnSuccQCI4 = 100 * (trafficData.getErabNbrsuccestab_4()) / (trafficData.getErabNbrattestab_4());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI4));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI5")) {
                float erab_ConnSuccQCI5;
                if ((trafficData.getErabNbrattestab_5()) == 0) {
                    erab_ConnSuccQCI5 = 100;
                } else {
                    erab_ConnSuccQCI5 = 100 * (trafficData.getErabNbrsuccestab_5()) / (trafficData.getErabNbrattestab_5());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI5));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI6")) {
                float erab_ConnSuccQCI6;
                if ((trafficData.getErabNbrattestab_6()) == 0) {
                    erab_ConnSuccQCI6 = 100;
                } else {
                    erab_ConnSuccQCI6 = 100 * (trafficData.getErabNbrsuccestab_6()) / (trafficData.getErabNbrattestab_6());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI6));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI7")) {
                float erab_ConnSuccQCI7;
                if ((trafficData.getErabNbrattestab_7()) == 0) {
                    erab_ConnSuccQCI7 = 100;
                } else {
                    erab_ConnSuccQCI7 = 100 * (trafficData.getErabNbrsuccestab_7()) / (trafficData.getErabNbrattestab_7());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI7));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI8")) {
                float erab_ConnSuccQCI8;
                if ((trafficData.getErabNbrattestab_8()) == 0) {
                    erab_ConnSuccQCI8 = 100;
                } else {
                    erab_ConnSuccQCI8 = 100 * (trafficData.getErabNbrsuccestab_8()) / (trafficData.getErabNbrattestab_8());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI8));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI9")) {

                float erab_ConnSuccQCI9;
                if ((trafficData.getErabNbrattestab_9()) == 0) {
                    erab_ConnSuccQCI9 = 100;
                } else {
                    erab_ConnSuccQCI9 = 100 * (trafficData.getErabNbrsuccestab_9()) / (trafficData.getErabNbrattestab_9());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI9));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI1")) {
                float wireConnQCI1;
                if ((trafficData.getErabNbrattestab_1()) == 0
                        || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI1 = 100;
                } else {
                    wireConnQCI1 = 100 * (trafficData.getErabNbrsuccestab_1()) / (trafficData.getErabNbrattestab_1()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI1));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI2")) {
                float wireConnQCI2;
                if ((trafficData.getErabNbrattestab_2()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI2 = 100;
                } else {
                    wireConnQCI2 = 100 * (trafficData.getErabNbrsuccestab_2()) / (trafficData.getErabNbrattestab_2()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI2));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI3")) {
                float wireConnQCI3;
                if ((trafficData.getErabNbrattestab_3()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI3 = 100;
                } else {
                    wireConnQCI3 = 100 * (trafficData.getErabNbrsuccestab_3()) / (trafficData.getErabNbrattestab_3()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI3));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI4")) {
                float wireConnQCI4;
                if ((trafficData.getErabNbrattestab_4()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI4 = 100;
                } else {
                    wireConnQCI4 = 100 * (trafficData.getErabNbrsuccestab_4()) / (trafficData.getErabNbrattestab_4())
                            * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI4));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI5")) {
                float wireConnQCI5;
                if ((trafficData.getErabNbrattestab_5()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI5 = 100;
                } else {
                    wireConnQCI5 = 100 * (trafficData.getErabNbrsuccestab_5()) / (trafficData.getErabNbrattestab_5())
                            * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI5));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI6")) {
                float wireConnQCI6;
                if ((trafficData.getErabNbrattestab_6()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI6 = 100;
                } else {
                    wireConnQCI6 = 100 * (trafficData.getErabNbrsuccestab_6()) / (trafficData.getErabNbrattestab_6())
                            * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI6));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI7")) {
                float wireConnQCI7;
                if ((trafficData.getErabNbrattestab_7()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI7 = 100;
                } else {
                    wireConnQCI7 = 100 * (trafficData.getErabNbrsuccestab_7()) / (trafficData.getErabNbrattestab_7())
                            * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI7));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI8")) {
                float wireConnQCI8;
                if ((trafficData.getErabNbrattestab_8()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI8 = 100;
                } else {
                    wireConnQCI8 = 100 * (trafficData.getErabNbrsuccestab_8()) / (trafficData.getErabNbrattestab_8())
                            * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI8));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI9")) {
                float wireConnQCI9;
                if ((trafficData.getErabNbrattestab_9()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI9 = 100;
                } else {
                    wireConnQCI9 = 100 * (trafficData.getErabNbrsuccestab_9()) / (trafficData.getErabNbrattestab_9())
                            * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI9));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI1_CellLevel")) {
                float erab_DropQCI1_CellLevel;
                if (((trafficData.getErabNbrleft_1()) + (trafficData.getErabNbrsuccestab_1()) + (trafficData.getErabNbrhoinc_1())) == 0) {
                    erab_DropQCI1_CellLevel = 100;
                } else {
                    erab_DropQCI1_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_1()) - (trafficData.getErabNbrreqrelenbNormal_1()) + (trafficData.getErabHofail_1()))
                            / ((trafficData.getErabNbrleft_1()) + (trafficData.getErabNbrsuccestab_1()) + (trafficData.getErabNbrhoinc_1()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI1_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI2_CellLevel")) {
                float erab_DropQCI2_CellLevel;
                if (((trafficData.getErabNbrleft_2()) + (trafficData.getErabNbrsuccestab_2()) + (trafficData.getErabNbrhoinc_2())) == 0) {
                    erab_DropQCI2_CellLevel = 100;
                } else {
                    erab_DropQCI2_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_2()) - (trafficData.getErabNbrreqrelenbNormal_2()) + (trafficData.getErabHofail_2()))
                            / ((trafficData.getErabNbrleft_2()) + (trafficData.getErabNbrsuccestab_2()) + (trafficData.getErabNbrhoinc_2()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI2_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI3_CellLevel")) {
                float erab_DropQCI3_CellLevel;
                if (((trafficData.getErabNbrleft_3()) + (trafficData.getErabNbrsuccestab_3()) + (trafficData.getErabNbrhoinc_3())) == 0) {
                    erab_DropQCI3_CellLevel = 100;
                } else {
                    erab_DropQCI3_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_3()) - (trafficData.getErabNbrreqrelenbNormal_3()) + (trafficData.getErabHofail_3()))
                            / ((trafficData.getErabNbrleft_3()) + (trafficData.getErabNbrsuccestab_3()) + (trafficData.getErabNbrhoinc_3()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI3_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI4_CellLevel")) {
                float erab_DropQCI4_CellLevel;
                if (((trafficData.getErabNbrleft_4()) + (trafficData.getErabNbrsuccestab_4()) + (trafficData.getErabNbrhoinc_4())) == 0) {
                    erab_DropQCI4_CellLevel = 100;
                } else {
                    erab_DropQCI4_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_4()) - (trafficData.getErabNbrreqrelenbNormal_4()) + (trafficData.getErabHofail_4()))
                            / ((trafficData.getErabNbrleft_4()) + (trafficData.getErabNbrsuccestab_4()) + (trafficData.getErabNbrhoinc_4()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI4_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI5_CellLevel")) {
                float erab_DropQCI5_CellLevel;
                if (((trafficData.getErabNbrleft_5()) + (trafficData.getErabNbrsuccestab_5()) + (trafficData.getErabNbrhoinc_5())) == 0) {
                    erab_DropQCI5_CellLevel = 100;
                } else {
                    erab_DropQCI5_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_5()) - (trafficData.getErabNbrreqrelenbNormal_5()) + (trafficData.getErabHofail_5()))
                            / ((trafficData.getErabNbrleft_5()) + (trafficData.getErabNbrsuccestab_5()) + (trafficData.getErabNbrhoinc_5()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI5_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI6_CellLevel")) {
                float erab_DropQCI6_CellLevel;
                if (((trafficData.getErabNbrleft_6()) + (trafficData.getErabNbrsuccestab_6()) + (trafficData.getErabNbrhoinc_6())) == 0) {
                    erab_DropQCI6_CellLevel = 100;
                } else {
                    erab_DropQCI6_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_6()) - (trafficData.getErabNbrreqrelenbNormal_6()) + (trafficData.getErabHofail_6()))
                            / ((trafficData.getErabNbrleft_6()) + (trafficData.getErabNbrsuccestab_6()) + (trafficData.getErabNbrhoinc_6()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI6_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI7_CellLevel")) {
                float erab_DropQCI7_CellLevel;
                if (((trafficData.getErabNbrleft_7()) + (trafficData.getErabNbrsuccestab_7()) + (trafficData.getErabNbrhoinc_7())) == 0) {
                    erab_DropQCI7_CellLevel = 100;
                } else {
                    erab_DropQCI7_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_7()) - (trafficData.getErabNbrreqrelenbNormal_7()) + (trafficData.getErabHofail_7()))
                            / ((trafficData.getErabNbrleft_7()) + (trafficData.getErabNbrsuccestab_7()) + (trafficData.getErabNbrhoinc_7()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI7_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI8_CellLevel")) {
                float erab_DropQCI8_CellLevel;
                if (((trafficData.getErabNbrleft_8()) + (trafficData.getErabNbrsuccestab_8()) + (trafficData.getErabNbrhoinc_8())) == 0) {
                    erab_DropQCI8_CellLevel = 100;
                } else {
                    erab_DropQCI8_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_8()) - (trafficData.getErabNbrreqrelenbNormal_8()) + (trafficData.getErabHofail_8()))
                            / ((trafficData.getErabNbrleft_8()) + (trafficData.getErabNbrsuccestab_8()) + (trafficData.getErabNbrhoinc_8()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI8_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI9_CellLevel")) {
                float erab_DropQCI9_CellLevel;
                if (((trafficData.getErabNbrleft_9()) + (trafficData.getErabNbrsuccestab_9()) + (trafficData.getErabNbrhoinc_9())) == 0) {
                    erab_DropQCI9_CellLevel = 100;
                } else {
                    erab_DropQCI9_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_9()) - (trafficData.getErabNbrreqrelenbNormal_9()) + (trafficData.getErabHofail_9()))
                            / ((trafficData.getErabNbrleft_9()) + (trafficData.getErabNbrsuccestab_9()) + (trafficData.getErabNbrhoinc_9()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI9_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI1")) {
                float erab_DropQCI1;
                if ((trafficData.getErabNbrsuccestab_1()) == 0) {
                    erab_DropQCI1 = 100;
                } else {
                    erab_DropQCI1 = ((trafficData.getErabNbrreqrelenb_1()) - (trafficData.getErabNbrreqrelenbNormal_1()) + (trafficData.getErabHofail_1())) / (trafficData.getErabNbrsuccestab_1());
                }
                resMap.put(colunmName, formatData(erab_DropQCI1));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI2")) {
                float erab_DropQCI2;
                if ((trafficData.getErabNbrsuccestab_2()) == 0) {
                    erab_DropQCI2 = 100;
                } else {
                    erab_DropQCI2 = ((trafficData.getErabNbrreqrelenb_2()) - (trafficData.getErabNbrreqrelenbNormal_2()) + (trafficData.getErabHofail_2())) / (trafficData.getErabNbrsuccestab_2());
                }
                resMap.put(colunmName, formatData(erab_DropQCI2));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI3")) {
                float erab_DropQCI3;
                if ((trafficData.getErabNbrsuccestab_3()) == 0) {
                    erab_DropQCI3 = 100;
                } else {
                    erab_DropQCI3 = ((trafficData.getErabNbrreqrelenb_3()) - (trafficData.getErabNbrreqrelenbNormal_3()) + (trafficData.getErabHofail_3())) / (trafficData.getErabNbrsuccestab_3());
                }
                resMap.put(colunmName, formatData(erab_DropQCI3));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI4")) {
                float erab_DropQCI4;
                if ((trafficData.getErabNbrsuccestab_4()) == 0) {
                    erab_DropQCI4 = 100;
                } else {
                    erab_DropQCI4 = ((trafficData.getErabNbrreqrelenb_4()) - (trafficData.getErabNbrreqrelenbNormal_4()) + (trafficData.getErabHofail_4())) / (trafficData.getErabNbrsuccestab_4());
                }
                resMap.put(colunmName, formatData(erab_DropQCI4));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI5")) {
                float erab_DropQCI5;
                if ((trafficData.getErabNbrsuccestab_5()) == 0) {
                    erab_DropQCI5 = 100;
                } else {
                    erab_DropQCI5 = ((trafficData.getErabNbrreqrelenb_5()) - (trafficData.getErabNbrreqrelenbNormal_5()) + (trafficData.getErabHofail_5())) / (trafficData.getErabNbrsuccestab_5());
                }
                resMap.put(colunmName, formatData(erab_DropQCI5));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI6")) {
                float erab_DropQCI6;
                if ((trafficData.getErabNbrsuccestab_6()) == 0) {
                    erab_DropQCI6 = 100;
                } else {
                    erab_DropQCI6 = ((trafficData.getErabNbrreqrelenb_6()) - (trafficData.getErabNbrreqrelenbNormal_6()) + (trafficData.getErabHofail_6())) / (trafficData.getErabNbrsuccestab_6());
                }
                resMap.put(colunmName, formatData(erab_DropQCI6));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI7")) {
                float erab_DropQCI7;
                if ((trafficData.getErabNbrsuccestab_7()) == 0) {
                    erab_DropQCI7 = 100;
                } else {
                    erab_DropQCI7 = ((trafficData.getErabNbrreqrelenb_7()) - (trafficData.getErabNbrreqrelenbNormal_7()) + (trafficData.getErabHofail_7())) / (trafficData.getErabNbrsuccestab_7());
                }
                resMap.put(colunmName, formatData(erab_DropQCI7));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI8")) {
                float erab_DropQCI8;
                if ((trafficData.getErabNbrsuccestab_8()) == 0) {
                    erab_DropQCI8 = 100;
                } else {
                    erab_DropQCI8 = ((trafficData.getErabNbrreqrelenb_8()) - (trafficData.getErabNbrreqrelenbNormal_8()) + (trafficData.getErabHofail_8())) / (trafficData.getErabNbrsuccestab_8());
                }
                resMap.put(colunmName, formatData(erab_DropQCI8));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI9")) {
                float erab_DropQCI9;
                if ((trafficData.getErabNbrsuccestab_9()) == 0) {
                    erab_DropQCI9 = 100;
                } else {
                    erab_DropQCI9 = ((trafficData.getErabNbrreqrelenb_9()) - (trafficData.getErabNbrreqrelenbNormal_9()) + (trafficData.getErabHofail_9())) / (trafficData.getErabNbrsuccestab_9());
                }
                resMap.put(colunmName, formatData(erab_DropQCI9));
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI1")) {
                float emUplinkSerBytesQCI1;
                emUplinkSerBytesQCI1 = (trafficData.getPdcpUpoctul_1());
                resMap.put(colunmName, emUplinkSerBytesQCI1);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI2")) {
                float emUplinkSerBytesQCI2;
                emUplinkSerBytesQCI2 = (trafficData.getPdcpUpoctul_2());
                resMap.put(colunmName, emUplinkSerBytesQCI2);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI3")) {
                float emUplinkSerBytesQCI3;
                emUplinkSerBytesQCI3 = (trafficData.getPdcpUpoctul_3());
                resMap.put(colunmName, emUplinkSerBytesQCI3);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI4")) {
                float emUplinkSerBytesQCI4;
                emUplinkSerBytesQCI4 = (trafficData.getPdcpUpoctul_4());
                resMap.put(colunmName, emUplinkSerBytesQCI4);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI5")) {
                float emUplinkSerBytesQCI5;
                emUplinkSerBytesQCI5 = (trafficData.getPdcpUpoctul_5());
                resMap.put(colunmName, emUplinkSerBytesQCI5);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI6")) {
                float emUplinkSerBytesQCI6;
                emUplinkSerBytesQCI6 = (trafficData.getPdcpUpoctul_6());
                resMap.put(colunmName, emUplinkSerBytesQCI6);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI7")) {
                float emUplinkSerBytesQCI7;
                emUplinkSerBytesQCI7 = (trafficData.getPdcpUpoctul_7());
                resMap.put(colunmName, emUplinkSerBytesQCI7);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI8")) {
                float emUplinkSerBytesQCI8;
                emUplinkSerBytesQCI8 = (trafficData.getPdcpUpoctul_8());
                resMap.put(colunmName, emUplinkSerBytesQCI8);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI9")) {
                float emUplinkSerBytesQCI9;
                emUplinkSerBytesQCI9 = (trafficData.getPdcpUpoctul_9());
                resMap.put(colunmName, emUplinkSerBytesQCI9);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI1")) {
                float emDownlinkSerBytesQCI1;
                emDownlinkSerBytesQCI1 = (trafficData.getPdcpUpoctdl_1());
                resMap.put(colunmName, emDownlinkSerBytesQCI1);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI2")) {
                float emDownlinkSerBytesQCI2;
                emDownlinkSerBytesQCI2 = (trafficData.getPdcpUpoctdl_2());
                resMap.put(colunmName, emDownlinkSerBytesQCI2);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI3")) {
                float emDownlinkSerBytesQCI3;
                emDownlinkSerBytesQCI3 = (trafficData.getPdcpUpoctdl_3());
                resMap.put(colunmName, emDownlinkSerBytesQCI3);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI4")) {
                float emDownlinkSerBytesQCI4;
                emDownlinkSerBytesQCI4 = (trafficData.getPdcpUpoctdl_4());
                resMap.put(colunmName, emDownlinkSerBytesQCI4);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI5")) {
                float emDownlinkSerBytesQCI5;
                emDownlinkSerBytesQCI5 = (trafficData.getPdcpUpoctdl_5());
                resMap.put(colunmName, emDownlinkSerBytesQCI5);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI6")) {
                float emDownlinkSerBytesQCI6;
                emDownlinkSerBytesQCI6 = (trafficData.getPdcpUpoctdl_6());
                resMap.put(colunmName, emDownlinkSerBytesQCI6);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI7")) {
                float emDownlinkSerBytesQCI7;
                emDownlinkSerBytesQCI7 = (trafficData.getPdcpUpoctdl_7());
                resMap.put(colunmName, emDownlinkSerBytesQCI7);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI8")) {
                float emDownlinkSerBytesQCI8;
                emDownlinkSerBytesQCI8 = (trafficData.getPdcpUpoctdl_8());
                resMap.put(colunmName, emDownlinkSerBytesQCI8);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI9")) {
                float emDownlinkSerBytesQCI9;
                emDownlinkSerBytesQCI9 = (trafficData.getPdcpUpoctdl_9());
                resMap.put(colunmName, emDownlinkSerBytesQCI9);
            }
            if (colunmName.trim().equalsIgnoreCase("wireDrop_CellLevel")) {
                float wireDrop_CellLevel;
                if ((trafficData.getContextSuccinitalsetup()) == 0) {
                    wireDrop_CellLevel = 0;
                } else {
                    wireDrop_CellLevel = ((trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal())) / (trafficData.getContextSuccinitalsetup()) * 100;
                }
                resMap.put(colunmName, formatData(wireDrop_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_EstabSucc_SuccTimes")) {
                float erab_EstabSucc_SuccTimes;
                erab_EstabSucc_SuccTimes = (trafficData.getErabNbrsuccestab());
                resMap.put(colunmName, erab_EstabSucc_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_EstabSucc_ReqTimes")) {
                float erab_EstabSucc_ReqTimes;
                erab_EstabSucc_ReqTimes = (trafficData.getErabNbrattestab());
                resMap.put(colunmName, erab_EstabSucc_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_Drop_ReqTimes_CellLevel")) {
                float erab_Drop_ReqTimes_CellLevel;
                erab_Drop_ReqTimes_CellLevel = (trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb());
                resMap.put(colunmName, erab_Drop_ReqTimes_CellLevel);
            }
            if (colunmName.trim().equalsIgnoreCase("switchSucc_SuccTimes")) {
                float switchSucc_SuccTimes;
                switchSucc_SuccTimes = (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb());
                resMap.put(colunmName, switchSucc_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("switchSucc_ReqTimes")) {
                float switchSucc_ReqTimes;
                switchSucc_ReqTimes = (trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb());
                resMap.put(colunmName, switchSucc_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("wireDrop_ReqTimes_CellLevel")) {
                float wireDrop_ReqTimes_CellLevel;
                wireDrop_ReqTimes_CellLevel = (trafficData.getContextSuccinitalsetup());
                resMap.put(colunmName, wireDrop_ReqTimes_CellLevel);
            }
            if (colunmName.trim().equalsIgnoreCase("wireConn_ReqTimes")) {
                float wireConn_ReqTimes;
                wireConn_ReqTimes = (trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab());
                resMap.put(colunmName, wireConn_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_Drop_DropTimes_CellLevel")) {
                float erab_Drop_DropTimes_CellLevel;
                erab_Drop_DropTimes_CellLevel = (trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail());
                resMap.put(colunmName, erab_Drop_DropTimes_CellLevel);
            }
            if (colunmName.trim().equalsIgnoreCase("wireConn_SuccTimes")) {
                float wireConn_SuccTimes;
                wireConn_SuccTimes = (trafficData.getErabNbrsuccestab()) * (trafficData.getRrcSuccconnestab());
                resMap.put(colunmName, wireConn_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnEstabSucc_SuccTimes")) {
                float rrc_ConnEstabSucc_SuccTimes;
                rrc_ConnEstabSucc_SuccTimes = (trafficData.getRrcSuccconnestab());
                resMap.put(colunmName, rrc_ConnEstabSucc_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnEstabSucc_ReqTimes")) {
                float rrc_ConnEstabSucc_ReqTimes;
                rrc_ConnEstabSucc_ReqTimes = (trafficData.getRrcAttconnestab());
                resMap.put(colunmName, rrc_ConnEstabSucc_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("wireDrop_DropTimes_CellLevel")) {
                float wireDrop_DropTimes_CellLevel;
                wireDrop_DropTimes_CellLevel = (trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal());
                resMap.put(colunmName, wireDrop_DropTimes_CellLevel);
            }
        }
        return resMap;
    }

    /**
     * 格式化数字
     *
     * @param data float
     * @return String
     */
    private String formatData(float data) {
        DecimalFormat df = new DecimalFormat("0.##%");
        return df.format(data / 100);
    }
    
}
