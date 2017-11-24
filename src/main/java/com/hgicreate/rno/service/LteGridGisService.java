package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.GridCoord;
import com.hgicreate.rno.domain.GridData;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public void getCellDataByGrid(String type, Long areaId, HttpServletResponse response) {

        String areaName = areaRepository.findById(areaId).getName();
        File file = new File(LocalDate.now().toString() + "-" + areaName + "-"
                + type.replace(",", "-") + "-grid.xlsx");
        String fileName = "";
        try {
           fileName = new String(file.getName().getBytes(),"iso-8859-1");
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

        List<GridData> gridData = gridDataRepository.findByGridTypeInAndAreaIdOrderByIdAsc
                (type.split(","), areaId);
        List<GridCoord> gridCoords = gridCoordRepository.findByGridIdInOrderByGridIdAsc(
                gridData.stream().map(GridData::getId).collect(Collectors.toList()));
        List<com.hgicreate.rno.domain.Cell> cells = lteCellGisRepository.findAllByAreaId(areaId);

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row;
        Cell cell;
        row = sheet.createRow((short) 0);
        cell = row.createCell(0);
        cell.setCellValue("网格类型");
        cell = row.createCell(1);
        cell.setCellValue("网格编码");
        cell = row.createCell(2);
        cell.setCellValue("小区ID");
        cell = row.createCell(3);
        cell.setCellValue("小区中文名");
        cell = row.createCell(4);
        cell.setCellValue("基站ID");
        cell = row.createCell(5);
        cell.setCellValue("ECI");
        cell = row.createCell(6);
        cell.setCellValue("厂家名称");
        cell = row.createCell(7);
        cell.setCellValue("跟踪区码");
        cell = row.createCell(8);
        cell.setCellValue("工作频段");
        cell = row.createCell(9);
        cell.setCellValue("小区带宽");
        cell = row.createCell(10);
        cell.setCellValue("频段指示");
        cell = row.createCell(11);
        cell.setCellValue("载频数量");
        cell = row.createCell(12);
        cell.setCellValue("中心载频的信道号");
        cell = row.createCell(13);
        cell.setCellValue("物理小区识别码");
        cell = row.createCell(14);
        cell.setCellValue("覆盖类型");
        cell = row.createCell(15);
        cell.setCellValue("覆盖场景");
        cell = row.createCell(16);
        cell.setCellValue("经度");
        cell = row.createCell(17);
        cell.setCellValue("纬度");
        cell = row.createCell(18);
        cell.setCellValue("方位角");
        cell = row.createCell(19);
        cell.setCellValue("电子下倾角");
        cell = row.createCell(20);
        cell.setCellValue("物理下倾角");
        cell = row.createCell(21);
        cell.setCellValue("总下倾角");
        cell = row.createCell(22);
        cell.setCellValue("天线挂高");
        cell = row.createCell(23);
        cell.setCellValue("是否拉远小区");
        cell = row.createCell(24);
        cell.setCellValue("是否关联状态库工参");
        cell = row.createCell(25);
        cell.setCellValue("是否关联状态库资源");
        cell = row.createCell(26);
        cell.setCellValue("站间距");
        int num = 0;
        int intersectnum;
        List<Point> plist;
        List<GridCoord> coords;
        List<Line> llist;
        Line line;
        for (com.hgicreate.rno.domain.Cell c : cells) {
            for (GridData grid : gridData) {
                final long currentGridId = grid.getId();
                //log.debug("当前gridId={}", grid.getId());
                //网格经纬度点集
                coords = gridCoords.stream().filter(coord -> coord.getGridId() == currentGridId)
                        .collect(Collectors.toList());
                //log.debug("coords.id={}", coords.size());

                plist = coords.stream().map(coord -> new Point(coord.getLongitude(), coord.getLatitude()))
                        .collect(Collectors.toList());
                //log.debug("点集={}", plist);

                //网格经纬度边集
                llist = new ArrayList<>();
                for (int l = 1; l < plist.size(); l++) {
                    line = new Line(plist.get(l - 1), plist.get(l));
                    llist.add(line);
                }
                //log.debug("边集={}", llist);

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
                    row = sheet.createRow((short) num + 1);
                    cell = row.createCell(0);
                    cell.setCellValue(grid.getGridType() == null ? "" : grid.getGridType());
                    cell = row.createCell(1);
                    cell.setCellValue(grid.getGridCode() == null ? "" : grid.getGridCode());
                    cell = row.createCell(2);
                    cell.setCellValue(c.getCellId() == null ? "" : c.getCellId());
                    cell = row.createCell(3);
                    cell.setCellValue(c.getCellName() == null ? "" : c.getCellName());
                    cell = row.createCell(4);
                    cell.setCellValue(c.getEnodebId() == null ? "" : c.getEnodebId());
                    cell = row.createCell(5);
                    cell.setCellValue(c.getEci() == null ? "" : c.getEci());
                    cell = row.createCell(6);
                    cell.setCellValue(c.getManufacturer() == null ? "" : c.getManufacturer());
                    cell = row.createCell(7);
                    cell.setCellValue(c.getTac() == null ? "" : c.getTac());
                    cell = row.createCell(8);
                    cell.setCellValue(c.getBandType() == null ? "" : c.getBandType());
                    cell = row.createCell(9);
                    cell.setCellValue(c.getBandWidth() == null ? "" : c.getBandWidth());
                    cell = row.createCell(10);
                    cell.setCellValue(c.getBandIndicator() == null ? "" : c.getBandIndicator());
                    cell = row.createCell(11);
                    cell.setCellValue(c.getBandAmount() == null ? "" : c.getBandAmount());
                    cell = row.createCell(12);
                    cell.setCellValue(c.getEarfcn() == null ? "" : c.getEarfcn());
                    cell = row.createCell(13);
                    cell.setCellValue(c.getPci() == null ? "" : c.getPci());
                    cell = row.createCell(14);
                    cell.setCellValue(c.getCoverType() == null ? "" : c.getCoverType());
                    cell = row.createCell(15);
                    cell.setCellValue(c.getCoverScene() == null ? "" : c.getCoverScene());
                    cell = row.createCell(16);
                    cell.setCellValue(c.getLongitude() == null ? "" : c.getLongitude());
                    cell = row.createCell(17);
                    cell.setCellValue(c.getLatitude() == null ? "" : c.getLatitude());
                    cell = row.createCell(18);
                    cell.setCellValue(c.getAzimuth() == null ? "" : c.getAzimuth());
                    cell = row.createCell(19);
                    cell.setCellValue(c.getEDowntilt() == null ? "" : c.getEDowntilt());
                    cell = row.createCell(20);
                    cell.setCellValue(c.getMDowntilt() == null ? "" : c.getMDowntilt());
                    cell = row.createCell(21);
                    cell.setCellValue(c.getTotalDowntilt() == null ? "" : c.getTotalDowntilt());
                    cell = row.createCell(22);
                    cell.setCellValue(c.getAntennaHeight() == null ? "" : c.getAntennaHeight());
                    cell = row.createCell(23);
                    cell.setCellValue(c.getRemoteCell() == null ? "" : c.getRemoteCell());
                    cell = row.createCell(24);
                    cell.setCellValue(c.getRelatedParam() == null ? "" : c.getRelatedParam());
                    cell = row.createCell(25);
                    cell.setCellValue(c.getRelatedResouce() == null ? "" : c.getRelatedResouce());
                    cell = row.createCell(26);
                    cell.setCellValue(c.getStationSpace() == null ? "" : c.getStationSpace());
                    num++;
                    break;
                }
            }
        }
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

        Line(Point point, Point point3) {
            this.point1 = point;
            this.point2 = point3;
        }

        Point getPoint1() {
            return point1;
        }

        Point getPoint2() {
            return point2;
        }

    }

}