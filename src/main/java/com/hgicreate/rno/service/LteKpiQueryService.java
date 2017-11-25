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

    public List<Map<String, Object>> queryResultForDownload(LteKpiQueryVM vm) throws ParseException {
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
        ListOrderedMap columnTitles = getColumnTitles(vm.getIndexColumnStr(), vm.getIndexColumnNameStr());

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
        List<String> columnNames = new ArrayList<String>();
        for (Object column : columnTitles.keySet()) {
            columns.add((String) column);
            columnNames.add((String) columnTitles.get(column));
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

    public ListOrderedMap getColumnTitles(String columnList, String columnNameList) {
        String[] columnArr = columnList.split(",");
        String[] columnNameArr = columnNameList.split(",");
        ListOrderedMap columnMap = new ListOrderedMap();
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
    public Map<String, Object> getLteStsIndex(LteTrafficData trafficData, String columnList) {
        Map<String, Object> resMap = calLteStsIndex(trafficData, columnList);
        return resMap;
    }

    private Map<String, Object> calLteStsIndex(LteTrafficData trafficData, String columnList) {

        String[] columnArr = {};
        if (columnList != null || columnList.length() > 0) {
            columnArr = columnList.split(",");
        }
        Map<String, Object> resMap = new HashMap<>();
        for (String colunmName : columnArr) {
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnEstabSucc")) {
                float rrc_ConnEstabSucc = 0;
                if (Float.valueOf(trafficData.getRrcAttconnestab()) == 0) {
                    rrc_ConnEstabSucc = 100;
                } else {
                    rrc_ConnEstabSucc = 100 * Float.valueOf(trafficData.getRrcSuccconnestab()) / Float.valueOf(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(rrc_ConnEstabSucc));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_EstabSucc")) {
                float erab_EstabSucc = 0;
                if (Float.valueOf(trafficData.getErabNbrattestab()) == 0) {
                    erab_EstabSucc = 100;
                } else {
                    erab_EstabSucc = 100 * getF(trafficData.getErabNbrsuccestab()) / getF(trafficData.getErabNbrattestab());
                }
                resMap.put(colunmName, formatData(erab_EstabSucc));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConn")) {
                float wireConn = 0;
                if (getF(trafficData.getErabNbrattestab()) * getF(trafficData.getRrcAttconnestab()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConn = 100;
                } else {
                    wireConn = 100 * getF(trafficData.getErabNbrsuccestab()) / getF(trafficData.getErabNbrattestab()) * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConn));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_Drop_CellLevel")) {

                float erab_Drop_CellLevel = 0;
                if (getF(trafficData.getErabNbrsuccestab()) == 0) {
                    erab_Drop_CellLevel = 0;
                } else {
                    erab_Drop_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb()) - getF(trafficData.getErabNbrreqrelenbNormal()) + getF(trafficData.getErabHofail()))
                            / (getF(trafficData.getErabNbrsuccestab()) + getF(trafficData.getHoSuccoutinterenbs1()) + getF(trafficData.getHoSuccoutinterenbx2()) + getF(trafficData.getHoAttoutintraenb()));
                }
                resMap.put(colunmName, formatData(erab_Drop_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnRebuild")) {
                float rrc_ConnRebuild = 0;
                if ((getF(trafficData.getRrcAttconnreestab()) + getF(trafficData.getRrcAttconnestab())) == 0) {
                    rrc_ConnRebuild = 0;
                } else {
                    rrc_ConnRebuild = 100 * getF(trafficData.getRrcAttconnreestab()) / (getF(trafficData.getRrcAttconnreestab()) + getF(trafficData.getRrcAttconnestab()));
                }
                resMap.put(colunmName, formatData(rrc_ConnRebuild));
            }
            if (colunmName.trim().equalsIgnoreCase("switchSucc")) {
                float switchSucc = 0;
                if ((getF(trafficData.getHoAttoutinterenbs1()) + getF(trafficData.getHoAttoutinterenbx2()) + getF(trafficData.getHoAttoutintraenb())) == 0) {
                    switchSucc = 100;
                } else {
                    switchSucc = 100 * (getF(trafficData.getHoSuccoutinterenbs1()) + getF(trafficData.getHoSuccoutinterenbx2()) + getF(trafficData.getHoSuccoutintraenb()))
                            / (getF(trafficData.getHoAttoutinterenbs1()) + getF(trafficData.getHoAttoutinterenbx2()) + getF(trafficData.getHoAttoutintraenb()));
                }
                resMap.put(colunmName, formatData(switchSucc));
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytes")) {
                float emUplinkSerBytes = 0;
                emUplinkSerBytes = getF(trafficData.getPdcpUpoctul());
                resMap.put(colunmName, emUplinkSerBytes);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytes")) {
                float emDownlinkSerBytes = 0;
                emDownlinkSerBytes = getF(trafficData.getPdcpUpoctdl());
                resMap.put(colunmName, emDownlinkSerBytes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI1")) {
                float erab_ConnSuccQCI1 = 0;
                if (getF(trafficData.getErabNbrattestab_1()) == 0) {
                    erab_ConnSuccQCI1 = 100;
                } else {
                    erab_ConnSuccQCI1 = 100 * getF(trafficData.getErabNbrsuccestab_1()) / getF(trafficData.getErabNbrattestab_1());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI1));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI2")) {

                float erab_ConnSuccQCI2 = 0;
                if (getF(trafficData.getErabNbrattestab_2()) == 0) {
                    erab_ConnSuccQCI2 = 100;
                } else {
                    erab_ConnSuccQCI2 = 100 * getF(trafficData.getErabNbrsuccestab_2()) / getF(trafficData.getErabNbrattestab_2());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI2));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI3")) {
                float erab_ConnSuccQCI3 = 0;
                if (getF(trafficData.getErabNbrattestab_3()) == 0) {
                    erab_ConnSuccQCI3 = 100;
                } else {
                    erab_ConnSuccQCI3 = 100 * getF(trafficData.getErabNbrsuccestab_3()) / getF(trafficData.getErabNbrattestab_3());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI3));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI4")) {
                float erab_ConnSuccQCI4 = 0;
                if (getF(trafficData.getErabNbrattestab_4()) == 0) {
                    erab_ConnSuccQCI4 = 100;
                } else {
                    erab_ConnSuccQCI4 = 100 * getF(trafficData.getErabNbrsuccestab_4()) / getF(trafficData.getErabNbrattestab_4());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI4));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI5")) {
                float erab_ConnSuccQCI5 = 0;
                if (getF(trafficData.getErabNbrattestab_5()) == 0) {
                    erab_ConnSuccQCI5 = 100;
                } else {
                    erab_ConnSuccQCI5 = 100 * getF(trafficData.getErabNbrsuccestab_5()) / getF(trafficData.getErabNbrattestab_5());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI5));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI6")) {
                float erab_ConnSuccQCI6 = 0;
                if (getF(trafficData.getErabNbrattestab_6()) == 0) {
                    erab_ConnSuccQCI6 = 100;
                } else {
                    erab_ConnSuccQCI6 = 100 * getF(trafficData.getErabNbrsuccestab_6()) / getF(trafficData.getErabNbrattestab_6());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI6));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI7")) {
                float erab_ConnSuccQCI7 = 0;
                if (getF(trafficData.getErabNbrattestab_7()) == 0) {
                    erab_ConnSuccQCI7 = 100;
                } else {
                    erab_ConnSuccQCI7 = 100 * getF(trafficData.getErabNbrsuccestab_7()) / getF(trafficData.getErabNbrattestab_7());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI7));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI8")) {
                float erab_ConnSuccQCI8 = 0;
                if (getF(trafficData.getErabNbrattestab_8()) == 0) {
                    erab_ConnSuccQCI8 = 100;
                } else {
                    erab_ConnSuccQCI8 = 100 * getF(trafficData.getErabNbrsuccestab_8()) / getF(trafficData.getErabNbrattestab_8());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI8));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_ConnSuccQCI9")) {

                float erab_ConnSuccQCI9 = 0;
                if (getF(trafficData.getErabNbrattestab_9()) == 0) {
                    erab_ConnSuccQCI9 = 100;
                } else {
                    erab_ConnSuccQCI9 = 100 * getF(trafficData.getErabNbrsuccestab_9()) / getF(trafficData.getErabNbrattestab_9());
                }
                resMap.put(colunmName, formatData(erab_ConnSuccQCI9));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI1")) {
                float wireConnQCI1 = 0;
                if (getF(trafficData.getErabNbrattestab_1()) == 0
                        || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI1 = 100;
                } else {
                    wireConnQCI1 = 100 * getF(trafficData.getErabNbrsuccestab_1()) / getF(trafficData.getErabNbrattestab_1()) * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI1));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI2")) {
                float wireConnQCI2 = 0;
                if (getF(trafficData.getErabNbrattestab_2()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI2 = 100;
                } else {
                    wireConnQCI2 = 100 * getF(trafficData.getErabNbrsuccestab_2()) / getF(trafficData.getErabNbrattestab_2()) * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI2));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI3")) {
                float wireConnQCI3 = 0;
                if (getF(trafficData.getErabNbrattestab_3()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI3 = 100;
                } else {
                    wireConnQCI3 = 100 * getF(trafficData.getErabNbrsuccestab_3()) / getF(trafficData.getErabNbrattestab_3()) * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI3));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI4")) {
                float wireConnQCI4 = 0;
                if (getF(trafficData.getErabNbrattestab_4()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI4 = 100;
                } else {
                    wireConnQCI4 = 100 * getF(trafficData.getErabNbrsuccestab_4()) / getF(trafficData.getErabNbrattestab_4())
                            * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI4));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI5")) {
                float wireConnQCI5 = 0;
                if (getF(trafficData.getErabNbrattestab_5()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI5 = 100;
                } else {
                    wireConnQCI5 = 100 * getF(trafficData.getErabNbrsuccestab_5()) / getF(trafficData.getErabNbrattestab_5())
                            * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI5));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI6")) {
                float wireConnQCI6 = 0;
                if (getF(trafficData.getErabNbrattestab_6()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI6 = 100;
                } else {
                    wireConnQCI6 = 100 * getF(trafficData.getErabNbrsuccestab_6()) / getF(trafficData.getErabNbrattestab_6())
                            * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI6));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI7")) {
                float wireConnQCI7 = 0;
                if (getF(trafficData.getErabNbrattestab_7()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI7 = 100;
                } else {
                    wireConnQCI7 = 100 * getF(trafficData.getErabNbrsuccestab_7()) / getF(trafficData.getErabNbrattestab_7())
                            * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI7));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI8")) {
                float wireConnQCI8 = 0;
                if (getF(trafficData.getErabNbrattestab_8()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI8 = 100;
                } else {
                    wireConnQCI8 = 100 * getF(trafficData.getErabNbrsuccestab_8()) / getF(trafficData.getErabNbrattestab_8())
                            * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI8));
            }
            if (colunmName.trim().equalsIgnoreCase("wireConnQCI9")) {
                float wireConnQCI9 = 0;
                if (getF(trafficData.getErabNbrattestab_9()) == 0 || getF(trafficData.getRrcAttconnestab()) == 0) {
                    wireConnQCI9 = 100;
                } else {
                    wireConnQCI9 = 100 * getF(trafficData.getErabNbrsuccestab_9()) / getF(trafficData.getErabNbrattestab_9())
                            * getF(trafficData.getRrcSuccconnestab()) / getF(trafficData.getRrcAttconnestab());
                }
                resMap.put(colunmName, formatData(wireConnQCI9));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI1_CellLevel")) {
                float erab_DropQCI1_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_1()) + getF(trafficData.getErabNbrsuccestab_1()) + getF(trafficData.getErabNbrhoinc_1())) == 0) {
                    erab_DropQCI1_CellLevel = 100;
                } else {
                    erab_DropQCI1_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_1()) - getF(trafficData.getErabNbrreqrelenbNormal_1()) + getF(trafficData.getErabHofail_1()))
                            / (getF(trafficData.getErabNbrleft_1()) + getF(trafficData.getErabNbrsuccestab_1()) + getF(trafficData.getErabNbrhoinc_1()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI1_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI2_CellLevel")) {
                float erab_DropQCI2_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_2()) + getF(trafficData.getErabNbrsuccestab_2()) + getF(trafficData.getErabNbrhoinc_2())) == 0) {
                    erab_DropQCI2_CellLevel = 100;
                } else {
                    erab_DropQCI2_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_2()) - getF(trafficData.getErabNbrreqrelenbNormal_2()) + getF(trafficData.getErabHofail_2()))
                            / (getF(trafficData.getErabNbrleft_2()) + getF(trafficData.getErabNbrsuccestab_2()) + getF(trafficData.getErabNbrhoinc_2()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI2_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI3_CellLevel")) {
                float erab_DropQCI3_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_3()) + getF(trafficData.getErabNbrsuccestab_3()) + getF(trafficData.getErabNbrhoinc_3())) == 0) {
                    erab_DropQCI3_CellLevel = 100;
                } else {
                    erab_DropQCI3_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_3()) - getF(trafficData.getErabNbrreqrelenbNormal_3()) + getF(trafficData.getErabHofail_3()))
                            / (getF(trafficData.getErabNbrleft_3()) + getF(trafficData.getErabNbrsuccestab_3()) + getF(trafficData.getErabNbrhoinc_3()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI3_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI4_CellLevel")) {
                float erab_DropQCI4_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_4()) + getF(trafficData.getErabNbrsuccestab_4()) + getF(trafficData.getErabNbrhoinc_4())) == 0) {
                    erab_DropQCI4_CellLevel = 100;
                } else {
                    erab_DropQCI4_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_4()) - getF(trafficData.getErabNbrreqrelenbNormal_4()) + getF(trafficData.getErabHofail_4()))
                            / (getF(trafficData.getErabNbrleft_4()) + getF(trafficData.getErabNbrsuccestab_4()) + getF(trafficData.getErabNbrhoinc_4()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI4_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI5_CellLevel")) {
                float erab_DropQCI5_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_5()) + getF(trafficData.getErabNbrsuccestab_5()) + getF(trafficData.getErabNbrhoinc_5())) == 0) {
                    erab_DropQCI5_CellLevel = 100;
                } else {
                    erab_DropQCI5_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_5()) - getF(trafficData.getErabNbrreqrelenbNormal_5()) + getF(trafficData.getErabHofail_5()))
                            / (getF(trafficData.getErabNbrleft_5()) + getF(trafficData.getErabNbrsuccestab_5()) + getF(trafficData.getErabNbrhoinc_5()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI5_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI6_CellLevel")) {
                float erab_DropQCI6_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_6()) + getF(trafficData.getErabNbrsuccestab_6()) + getF(trafficData.getErabNbrhoinc_6())) == 0) {
                    erab_DropQCI6_CellLevel = 100;
                } else {
                    erab_DropQCI6_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_6()) - getF(trafficData.getErabNbrreqrelenbNormal_6()) + getF(trafficData.getErabHofail_6()))
                            / (getF(trafficData.getErabNbrleft_6()) + getF(trafficData.getErabNbrsuccestab_6()) + getF(trafficData.getErabNbrhoinc_6()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI6_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI7_CellLevel")) {
                float erab_DropQCI7_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_7()) + getF(trafficData.getErabNbrsuccestab_7()) + getF(trafficData.getErabNbrhoinc_7())) == 0) {
                    erab_DropQCI7_CellLevel = 100;
                } else {
                    erab_DropQCI7_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_7()) - getF(trafficData.getErabNbrreqrelenbNormal_7()) + getF(trafficData.getErabHofail_7()))
                            / (getF(trafficData.getErabNbrleft_7()) + getF(trafficData.getErabNbrsuccestab_7()) + getF(trafficData.getErabNbrhoinc_7()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI7_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI8_CellLevel")) {
                float erab_DropQCI8_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_8()) + getF(trafficData.getErabNbrsuccestab_8()) + getF(trafficData.getErabNbrhoinc_8())) == 0) {
                    erab_DropQCI8_CellLevel = 100;
                } else {
                    erab_DropQCI8_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_8()) - getF(trafficData.getErabNbrreqrelenbNormal_8()) + getF(trafficData.getErabHofail_8()))
                            / (getF(trafficData.getErabNbrleft_8()) + getF(trafficData.getErabNbrsuccestab_8()) + getF(trafficData.getErabNbrhoinc_8()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI8_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI9_CellLevel")) {
                float erab_DropQCI9_CellLevel = 0;
                if ((getF(trafficData.getErabNbrleft_9()) + getF(trafficData.getErabNbrsuccestab_9()) + getF(trafficData.getErabNbrhoinc_9())) == 0) {
                    erab_DropQCI9_CellLevel = 100;
                } else {
                    erab_DropQCI9_CellLevel = 100 * (getF(trafficData.getErabNbrreqrelenb_9()) - getF(trafficData.getErabNbrreqrelenbNormal_9()) + getF(trafficData.getErabHofail_9()))
                            / (getF(trafficData.getErabNbrleft_9()) + getF(trafficData.getErabNbrsuccestab_9()) + getF(trafficData.getErabNbrhoinc_9()));
                }
                resMap.put(colunmName, formatData(erab_DropQCI9_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI1")) {
                float erab_DropQCI1 = 0;
                if (getF(trafficData.getErabNbrsuccestab_1()) == 0) {
                    erab_DropQCI1 = 100;
                } else {
                    erab_DropQCI1 = (getF(trafficData.getErabNbrreqrelenb_1()) - getF(trafficData.getErabNbrreqrelenbNormal_1()) + getF(trafficData.getErabHofail_1())) / getF(trafficData.getErabNbrsuccestab_1());
                }
                resMap.put(colunmName, formatData(erab_DropQCI1));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI2")) {
                float erab_DropQCI2 = 0;
                if (getF(trafficData.getErabNbrsuccestab_2()) == 0) {
                    erab_DropQCI2 = 100;
                } else {
                    erab_DropQCI2 = (getF(trafficData.getErabNbrreqrelenb_2()) - getF(trafficData.getErabNbrreqrelenbNormal_2()) + getF(trafficData.getErabHofail_2())) / getF(trafficData.getErabNbrsuccestab_2());
                }
                resMap.put(colunmName, formatData(erab_DropQCI2));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI3")) {
                float erab_DropQCI3 = 0;
                if (getF(trafficData.getErabNbrsuccestab_3()) == 0) {
                    erab_DropQCI3 = 100;
                } else {
                    erab_DropQCI3 = (getF(trafficData.getErabNbrreqrelenb_3()) - getF(trafficData.getErabNbrreqrelenbNormal_3()) + getF(trafficData.getErabHofail_3())) / getF(trafficData.getErabNbrsuccestab_3());
                }
                resMap.put(colunmName, formatData(erab_DropQCI3));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI4")) {
                float erab_DropQCI4 = 0;
                if (getF(trafficData.getErabNbrsuccestab_4()) == 0) {
                    erab_DropQCI4 = 100;
                } else {
                    erab_DropQCI4 = (getF(trafficData.getErabNbrreqrelenb_4()) - getF(trafficData.getErabNbrreqrelenbNormal_4()) + getF(trafficData.getErabHofail_4())) / getF(trafficData.getErabNbrsuccestab_4());
                }
                resMap.put(colunmName, formatData(erab_DropQCI4));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI5")) {
                float erab_DropQCI5 = 0;
                if (getF(trafficData.getErabNbrsuccestab_5()) == 0) {
                    erab_DropQCI5 = 100;
                } else {
                    erab_DropQCI5 = (getF(trafficData.getErabNbrreqrelenb_5()) - getF(trafficData.getErabNbrreqrelenbNormal_5()) + getF(trafficData.getErabHofail_5())) / getF(trafficData.getErabNbrsuccestab_5());
                }
                resMap.put(colunmName, formatData(erab_DropQCI5));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI6")) {
                float erab_DropQCI6 = 0;
                if (getF(trafficData.getErabNbrsuccestab_6()) == 0) {
                    erab_DropQCI6 = 100;
                } else {
                    erab_DropQCI6 = (getF(trafficData.getErabNbrreqrelenb_6()) - getF(trafficData.getErabNbrreqrelenbNormal_6()) + getF(trafficData.getErabHofail_6())) / getF(trafficData.getErabNbrsuccestab_6());
                }
                resMap.put(colunmName, formatData(erab_DropQCI6));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI7")) {
                float erab_DropQCI7 = 0;
                if (getF(trafficData.getErabNbrsuccestab_7()) == 0) {
                    erab_DropQCI7 = 100;
                } else {
                    erab_DropQCI7 = (getF(trafficData.getErabNbrreqrelenb_7()) - getF(trafficData.getErabNbrreqrelenbNormal_7()) + getF(trafficData.getErabHofail_7())) / getF(trafficData.getErabNbrsuccestab_7());
                }
                resMap.put(colunmName, formatData(erab_DropQCI7));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI8")) {
                float erab_DropQCI8 = 0;
                if (getF(trafficData.getErabNbrsuccestab_8()) == 0) {
                    erab_DropQCI8 = 100;
                } else {
                    erab_DropQCI8 = (getF(trafficData.getErabNbrreqrelenb_8()) - getF(trafficData.getErabNbrreqrelenbNormal_8()) + getF(trafficData.getErabHofail_8())) / getF(trafficData.getErabNbrsuccestab_8());
                }
                resMap.put(colunmName, formatData(erab_DropQCI8));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_DropQCI9")) {
                float erab_DropQCI9 = 0;
                if (getF(trafficData.getErabNbrsuccestab_9()) == 0) {
                    erab_DropQCI9 = 100;
                } else {
                    erab_DropQCI9 = (getF(trafficData.getErabNbrreqrelenb_9()) - getF(trafficData.getErabNbrreqrelenbNormal_9()) + getF(trafficData.getErabHofail_9())) / getF(trafficData.getErabNbrsuccestab_9());
                }
                resMap.put(colunmName, formatData(erab_DropQCI9));
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI1")) {
                float emUplinkSerBytesQCI1 = 0;
                emUplinkSerBytesQCI1 = getF(trafficData.getPdcpUpoctul_1());
                resMap.put(colunmName, emUplinkSerBytesQCI1);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI2")) {
                float emUplinkSerBytesQCI2 = 0;
                emUplinkSerBytesQCI2 = getF(trafficData.getPdcpUpoctul_2());
                resMap.put(colunmName, emUplinkSerBytesQCI2);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI3")) {
                float emUplinkSerBytesQCI3 = 0;
                emUplinkSerBytesQCI3 = getF(trafficData.getPdcpUpoctul_3());
                resMap.put(colunmName, emUplinkSerBytesQCI3);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI4")) {
                float emUplinkSerBytesQCI4 = 0;
                emUplinkSerBytesQCI4 = getF(trafficData.getPdcpUpoctul_4());
                resMap.put(colunmName, emUplinkSerBytesQCI4);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI5")) {
                float emUplinkSerBytesQCI5 = 0;
                emUplinkSerBytesQCI5 = getF(trafficData.getPdcpUpoctul_5());
                resMap.put(colunmName, emUplinkSerBytesQCI5);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI6")) {
                float emUplinkSerBytesQCI6 = 0;
                emUplinkSerBytesQCI6 = getF(trafficData.getPdcpUpoctul_6());
                resMap.put(colunmName, emUplinkSerBytesQCI6);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI7")) {
                float emUplinkSerBytesQCI7 = 0;
                emUplinkSerBytesQCI7 = getF(trafficData.getPdcpUpoctul_7());
                resMap.put(colunmName, emUplinkSerBytesQCI7);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI8")) {
                float emUplinkSerBytesQCI8 = 0;
                emUplinkSerBytesQCI8 = getF(trafficData.getPdcpUpoctul_8());
                resMap.put(colunmName, emUplinkSerBytesQCI8);
            }
            if (colunmName.trim().equalsIgnoreCase("emUplinkSerBytesQCI9")) {
                float emUplinkSerBytesQCI9 = 0;
                emUplinkSerBytesQCI9 = getF(trafficData.getPdcpUpoctul_9());
                resMap.put(colunmName, emUplinkSerBytesQCI9);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI1")) {
                float emDownlinkSerBytesQCI1 = 0;
                emDownlinkSerBytesQCI1 = getF(trafficData.getPdcpUpoctdl_1());
                resMap.put(colunmName, emDownlinkSerBytesQCI1);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI2")) {
                float emDownlinkSerBytesQCI2 = 0;
                emDownlinkSerBytesQCI2 = getF(trafficData.getPdcpUpoctdl_2());
                resMap.put(colunmName, emDownlinkSerBytesQCI2);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI3")) {
                float emDownlinkSerBytesQCI3 = 0;
                emDownlinkSerBytesQCI3 = getF(trafficData.getPdcpUpoctdl_3());
                resMap.put(colunmName, emDownlinkSerBytesQCI3);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI4")) {
                float emDownlinkSerBytesQCI4 = 0;
                emDownlinkSerBytesQCI4 = getF(trafficData.getPdcpUpoctdl_4());
                resMap.put(colunmName, emDownlinkSerBytesQCI4);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI5")) {
                float emDownlinkSerBytesQCI5 = 0;
                emDownlinkSerBytesQCI5 = getF(trafficData.getPdcpUpoctdl_5());
                resMap.put(colunmName, emDownlinkSerBytesQCI5);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI6")) {
                float emDownlinkSerBytesQCI6 = 0;
                emDownlinkSerBytesQCI6 = getF(trafficData.getPdcpUpoctdl_6());
                resMap.put(colunmName, emDownlinkSerBytesQCI6);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI7")) {
                float emDownlinkSerBytesQCI7 = 0;
                emDownlinkSerBytesQCI7 = getF(trafficData.getPdcpUpoctdl_7());
                resMap.put(colunmName, emDownlinkSerBytesQCI7);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI8")) {
                float emDownlinkSerBytesQCI8 = 0;
                emDownlinkSerBytesQCI8 = getF(trafficData.getPdcpUpoctdl_8());
                resMap.put(colunmName, emDownlinkSerBytesQCI8);
            }
            if (colunmName.trim().equalsIgnoreCase("emDownlinkSerBytesQCI9")) {
                float emDownlinkSerBytesQCI9 = 0;
                emDownlinkSerBytesQCI9 = getF(trafficData.getPdcpUpoctdl_9());
                resMap.put(colunmName, emDownlinkSerBytesQCI9);
            }
            if (colunmName.trim().equalsIgnoreCase("wireDrop_CellLevel")) {
                float wireDrop_CellLevel = 0;
                if (getF(trafficData.getContextSuccinitalsetup()) == 0) {
                    wireDrop_CellLevel = 0;
                } else {
                    wireDrop_CellLevel = (getF(trafficData.getContextAttrelenb()) - getF(trafficData.getContextAttrelenbNormal())) / getF(trafficData.getContextSuccinitalsetup()) * 100;
                }
                resMap.put(colunmName, formatData(wireDrop_CellLevel));
            }
            if (colunmName.trim().equalsIgnoreCase("erab_EstabSucc_SuccTimes")) {
                float erab_EstabSucc_SuccTimes = 0;
                erab_EstabSucc_SuccTimes = getF(trafficData.getErabNbrsuccestab());
                resMap.put(colunmName, erab_EstabSucc_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_EstabSucc_ReqTimes")) {
                float erab_EstabSucc_ReqTimes = 0;
                erab_EstabSucc_ReqTimes = getF(trafficData.getErabNbrattestab());
                resMap.put(colunmName, erab_EstabSucc_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_Drop_ReqTimes_CellLevel")) {
                float erab_Drop_ReqTimes_CellLevel = 0;
                erab_Drop_ReqTimes_CellLevel = getF(trafficData.getErabNbrsuccestab()) + getF(trafficData.getHoSuccoutinterenbs1()) + getF(trafficData.getHoSuccoutinterenbx2()) + getF(trafficData.getHoAttoutintraenb());
                resMap.put(colunmName, erab_Drop_ReqTimes_CellLevel);
            }
            if (colunmName.trim().equalsIgnoreCase("switchSucc_SuccTimes")) {
                float switchSucc_SuccTimes = 0;
                switchSucc_SuccTimes = getF(trafficData.getHoSuccoutinterenbs1()) + getF(trafficData.getHoSuccoutinterenbx2()) + getF(trafficData.getHoSuccoutintraenb());
                resMap.put(colunmName, switchSucc_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("switchSucc_ReqTimes")) {
                float switchSucc_ReqTimes = 0;
                switchSucc_ReqTimes = getF(trafficData.getHoAttoutinterenbs1()) + getF(trafficData.getHoAttoutinterenbx2()) + getF(trafficData.getHoAttoutintraenb());
                resMap.put(colunmName, switchSucc_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("wireDrop_ReqTimes_CellLevel")) {
                float wireDrop_ReqTimes_CellLevel = 0;
                wireDrop_ReqTimes_CellLevel = getF(trafficData.getContextSuccinitalsetup());
                resMap.put(colunmName, wireDrop_ReqTimes_CellLevel);
            }
            if (colunmName.trim().equalsIgnoreCase("wireConn_ReqTimes")) {
                float wireConn_ReqTimes = 0;
                wireConn_ReqTimes = getF(trafficData.getErabNbrattestab()) * getF(trafficData.getRrcAttconnestab());
                resMap.put(colunmName, wireConn_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("erab_Drop_DropTimes_CellLevel")) {
                float erab_Drop_DropTimes_CellLevel = 0;
                erab_Drop_DropTimes_CellLevel = getF(trafficData.getErabNbrreqrelenb()) - getF(trafficData.getErabNbrreqrelenbNormal()) + getF(trafficData.getErabHofail());
                resMap.put(colunmName, erab_Drop_DropTimes_CellLevel);
            }
            if (colunmName.trim().equalsIgnoreCase("wireConn_SuccTimes")) {
                float wireConn_SuccTimes = 0;
                wireConn_SuccTimes = getF(trafficData.getErabNbrsuccestab()) * getF(trafficData.getRrcSuccconnestab());
                resMap.put(colunmName, wireConn_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnEstabSucc_SuccTimes")) {
                float rrc_ConnEstabSucc_SuccTimes = 0;
                rrc_ConnEstabSucc_SuccTimes = getF(trafficData.getRrcSuccconnestab());
                resMap.put(colunmName, rrc_ConnEstabSucc_SuccTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("rrc_ConnEstabSucc_ReqTimes")) {
                float rrc_ConnEstabSucc_ReqTimes = 0;
                rrc_ConnEstabSucc_ReqTimes = getF(trafficData.getRrcAttconnestab());
                resMap.put(colunmName, rrc_ConnEstabSucc_ReqTimes);
            }
            if (colunmName.trim().equalsIgnoreCase("wireDrop_DropTimes_CellLevel")) {
                float wireDrop_DropTimes_CellLevel = 0;
                wireDrop_DropTimes_CellLevel = getF(trafficData.getContextAttrelenb()) - getF(trafficData.getContextAttrelenbNormal());
                resMap.put(colunmName, wireDrop_DropTimes_CellLevel);
            }
        }
        return resMap;
    }

    /**
     * 格式化数字
     *
     * @param data
     * @return
     */
    private String formatData(float data) {
        DecimalFormat df = new DecimalFormat("0.##%");
        return df.format(data / 100);
    }

    private float getF(String prName) {
        return Float.valueOf(prName);
    }
}
