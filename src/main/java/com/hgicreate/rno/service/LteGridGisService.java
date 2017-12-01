package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.GridCoord;
import com.hgicreate.rno.domain.GridData;
import com.hgicreate.rno.domain.LteCell;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.GridCoordRepository;
import com.hgicreate.rno.repository.GridDataRepository;
import com.hgicreate.rno.repository.LteCellGisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LteGridGisService {
    private final GridDataRepository gridDataRepository;
    private final GridCoordRepository gridCoordRepository;
    private final AreaRepository areaRepository;
    private final LteCellGisRepository lteCellGisRepository;

    public LteGridGisService(GridDataRepository gridDataRepository,
                             GridCoordRepository gridCoordRepository,
                             AreaRepository areaRepository,
                             LteCellGisRepository lteCellGisRepository) {
        this.gridDataRepository = gridDataRepository;
        this.gridCoordRepository = gridCoordRepository;
        this.areaRepository = areaRepository;
        this.lteCellGisRepository = lteCellGisRepository;
    }

    private final Map<String, String> TITLE = new LinkedHashMap<String, String>() {{
        put("gridType", "网格类型");
        put("gridCode", "网格编码");
        put("cellId", "小区ID");
        put("cellName", "小区中文名");
        put("enodebId", "基站ID");
        put("eci", "ECI");
        put("manufacturer", "厂家名称");
        put("tac", "跟踪区码");
        put("bandType", "工作频段");
        put("bandWidth", "小区带宽");
        put("bandIndicator", "频段指示");
        put("bandAmount", "载频数量");
        put("earfcn", "中心载频的信道号");
        put("pci", "物理小区识别码");
        put("coverType", "覆盖类型");
        put("coverScene", "覆盖场景");
        put("longitude", "经度");
        put("latitude", "纬度");
        put("azimuth", "方位角");
        put("eDowntilt", "电子下倾角");
        put("mDowntilt", "物理下倾角");
        put("totalDowntilt", "总下倾角");
        put("antennaHeight", "天线挂高");
        put("remoteCell", "是否拉远小区");
        put("relatedParam", "是否关联状态库工参");
        put("relatedResouce", "是否关联状态库资源");
        put("stationSpace", "站间距");
    }};

    public void getCellDataByGrid(String type, Long areaId, HttpServletResponse response) {

        String areaName = areaRepository.findById(areaId).getName();
        File file = new File(LocalDate.now().toString() + "-" + areaName + "-"
                + type.replace(",", "-") + "类网格小区数据.xlsx");
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

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row;
        Cell cell;
        row = sheet.createRow(0);
        int x = 0;
        for (String title : TITLE.values()) {
            cell = row.createCell(x);
            cell.setCellValue(title);
            x++;
        }

        int num = 0;
        int intersectnum;
        List<Point> plist;
        List<GridCoord> coords;
        List<Line> llist;
        Line line;
        List<GridData> gridData;
        List<GridCoord> gridCoords;
        Method m;
        Object value;

        log.debug("开始匹配网格小区数据");
        List<LteCell> lteCells = lteCellGisRepository.findAllByAreaId(areaId);
        for (String gt : type.split(",")) {
            gridData = gridDataRepository.findByGridTypeAndAreaIdOrderByIdAsc
                    (gt, areaId);
            gridCoords = gridCoordRepository.findByGridIdInOrderByGridIdAsc(
                    gridData.stream().map(GridData::getId).collect(Collectors.toList()));

            for (LteCell c : lteCells) {
                for (GridData grid : gridData) {
                    final long currentGridId = grid.getId();
                    //网格经纬度点集
                    coords = gridCoords.stream().filter(coord -> coord.getGridId() == currentGridId)
                            .collect(Collectors.toList());

                    plist = coords.stream().map(coord -> new Point(coord.getLongitude(),
                            coord.getLatitude())).collect(Collectors.toList());

                    //网格经纬度边集
                    llist = new ArrayList<>();
                    for (int l = 1; l < plist.size(); l++) {
                        line = new Line(plist.get(l - 1), plist.get(l));
                        llist.add(line);
                    }

                    intersectnum = 0;//射线与网格边交点个数
                    for (Line aLlist : llist) {
                        //在当前网格边集中找出与过当前小区位置的水平线相交的网格边
                        if ((aLlist.getPoint1().getLat() <= Double.parseDouble(c.getLatitude())
                                && Double.parseDouble(c.getLatitude()) <= aLlist.getPoint2().getLat())
                                || (aLlist.getPoint2().getLat() <= Double.parseDouble(c.getLatitude())
                                && Double.parseDouble(c.getLatitude()) <= aLlist.getPoint1().getLat())) {

                            //斜率
                            Double slope = (aLlist.getPoint1().getLng() - aLlist.getPoint2().getLng()) /
                                    (aLlist.getPoint1().getLat() - aLlist.getPoint2().getLat());
                            //水平线与网格边交点的经度
                            Double intersectlng = slope * (Double.parseDouble(c.getLatitude()) -
                                    aLlist.getPoint1().getLat()) + aLlist.getPoint1().getLng();
                            //选取右射线
                            if (Double.parseDouble(c.getLongitude()) < intersectlng) {
                                intersectnum++;
                            }
                        }
                    }
                    //过滤不相交以及交点个数为偶数的网格
                    if (intersectnum != 0 && intersectnum % 2 != 0) {
                        row = sheet.createRow(num + 1);
                        int n = 0;
                        for (String name : TITLE.keySet()) {
                            cell = row.createCell(n);
                            try {
                                if (n == 0) {
                                    cell.setCellValue(format(grid.getGridType()));
                                } else if (n == 1) {
                                    cell.setCellValue(format(grid.getGridCode()));
                                } else {
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                    m = c.getClass().getMethod("get" + name);
                                    value = m.invoke(c);
                                    cell.setCellValue(format(value));
                                }
                            } catch (NoSuchMethodException |
                                    InvocationTargetException |
                                    IllegalAccessException e) {
                                log.error("生成文件过程出错！");
                                e.printStackTrace();
                            }
                            n++;
                        }
                        num++;
                        if (num % 1000 == 0) {
                            log.debug("已经匹配到{}条小区数据", num);
                        }
                        break;
                    }
                }
            }
        }
        log.debug("匹配完成！");
        //最终写入文件
        try {
            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (os != null) {
                os.flush();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class Point {

        private double lng;
        private double lat;

        Point(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        double getLng() {
            return lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

    }

    private class Line {

        private Point point1;
        private Point point2;

        Line(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        Point getPoint1() {
            return point1;
        }

        Point getPoint2() {
            return point2;
        }

    }

    private String format(Object o) {
        return o == null ? "" : o.toString();
    }

}