package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmDynamicCoverageMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xiao.sz
 */
@Service
@Slf4j
public class GsmDynamicCoverageServiceImpl implements GsmDynamicCoverageService {

    // 曲线点距离服务小区的长度系数0.000005
    private static double DefaultImgSize = 0.000005;
    // 服务小区评估方向的长度系数，经调试0.00003 较好
    private static double EvalCoeff = 0.00003;
    //控制点收缩系数 ，经调试0.6 较好
    private static float Scale = 0.6f;
    //曲线密度0.01
    private static double Step = 0.01;
    // 矢量长度系数0.5
    private static double DefaultImgSizeCoff = 0.5;

    @Autowired
    private GsmDynamicCoverageMapper gsmDynamicCoverageMapper;

    /**
     * 获取画小区动态覆盖图所需的数据
     *
     * @param enName    小区英文名
     * @param cellId    小区Id
     * @param cityId    城市Id
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    @Override
    public Map<String, List<Map<String, Object>>> getDynaCoverageDataByCityAndDate(
            String enName, String cellId, long cityId, String startDate, String endDate) {

        log.debug("getDynaCoverageDataByCityAndDate, enName=" + enName + ", cityId=" + cityId
                + ", startDate=" + startDate + ", endDate=" + endDate);

        long startTime = System.currentTimeMillis();
        //先判断小区是华为小区还是爱立信小区
        String sCell = "'" + enName + "'";
        String manu = checkCellIsHwOrEri(sCell);
        if (manu == null) {
            log.error("不能判断该小区是哪个厂家！ enName=" + enName);
            return null;
        }
        //RELSS>-12
        List<Map<String, Object>> dynaCoverData12 = new ArrayList<>();
        //RELSS>3
        List<Map<String, Object>> dynaCoverData3 = new ArrayList<>();

        if (("1").equals(manu)) {
            //爱立信小区
            dynaCoverData12 = queryEriDataFromOracle(cityId, cellId, startDate, endDate, "-12");
            dynaCoverData3 = queryEriDataFromOracle(cityId, cellId, startDate, endDate, "+3");
        } else if (("2").equals(manu)) {
            //华为小区
            dynaCoverData12 = queryHwDataFromOracle(cityId, enName, cellId, startDate, endDate, "-12");
            dynaCoverData3 = queryHwDataFromOracle(cityId, enName, cellId, startDate, endDate, "+3");
        }
        boolean resultIsNullOrHasNothing = (dynaCoverData12 == null || dynaCoverData12.size() == 0)
                && (dynaCoverData3 == null || dynaCoverData3.size() == 0);
        if (resultIsNullOrHasNothing) {
            return null;
        }
        long endTime = System.currentTimeMillis();
        log.debug("从数据库读取(RELSS>-12)数据量：" + dynaCoverData12.size()
                + ";(RELSS>3)数据量：" + dynaCoverData3.size() + "; 共耗时：" + (endTime - startTime));

        //获取画(relss>-12)图形所需曲线点坐标集合
        Map<String, List<Map<String, Object>>> res12
                = calcDynaCoveragePointsData(dynaCoverData12);
        //获取画(relss>3)图形所需曲线点坐标集合
        Map<String, List<Map<String, Object>>> res3
                = calcDynaCoveragePointsData(dynaCoverData3);

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("vectorPoint_12", res12.get("vectorPoint"));
        result.put("vectorPoint_3", res3.get("vectorPoint"));
        result.put("curvePoints_12", res12.get("curvePoints"));
        result.put("curvePoints_3", res3.get("curvePoints"));
        return result;
    }

    @Override
    public List<Map<String, Object>> getNcellDetailsByCellAndAreaId(String cellId, long areaId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("cellId", "'" + cellId + "'");
        map.put("areaId", areaId);
        return gsmDynamicCoverageMapper.getNcellDetailsByCellandCityId(map);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ReferencePoint {
        double lng;
        double lat;

        @Override
        public String toString() {
            return "ReferencePoint [lng=" + lng + ", lat="
                    + lat + "]";
        }
    }

    /**
     * map根据value值,从大到小排序
     */
    private Map<String, Double> sortMapByValue(Map<String, Double> unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        list.sort((Object o1, Object o2) -> ((Comparable) ((Map.Entry) (o2)).getValue())
                .compareTo(((Map.Entry) (o1)).getValue()));
        Map<String, Double> sortedMap = new HashMap<>();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put((String) entry.getKey(), (double) entry.getValue());
        }
        return sortedMap;
    }

    /**
     * 获取画图所需曲线点坐标集合
     */
    private Map<String, List<Map<String, Object>>> calcDynaCoveragePointsData(
            List<Map<String, Object>> dynaCoverData) {
        long startTime = System.currentTimeMillis();
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        double cellLng = Double.parseDouble(dynaCoverData.get(0).get("CELL_LNG").toString());
        double cellLat = Double.parseDouble(dynaCoverData.get(0).get("CELL_LAT").toString());

        //图形大小系数取最强前六邻区与本小区的距离平均值
        double imgSize = 0.0;
        //取出最强6 邻区的相关数据，用于获取距离平均值
        Map<String, Map<String, Double>> ncellIdToLngLat = new HashMap<>();
        Map<String, Double> lnglatMap = null;
        Map<String, Double> ncellIdToVal = new HashMap<>();
        for (Map<String, Object> one : dynaCoverData) {
            double val = Double.parseDouble(one.get("VAL").toString());
            double lng = Double.parseDouble(one.get("LNG").toString());
            double lat = Double.parseDouble(one.get("LAT").toString());
            String ncellId = one.get("NCELL_ID").toString();
            ncellIdToVal.put(ncellId, val);
            lnglatMap = new HashMap<>(2);
            lnglatMap.put("LNG", lng);
            lnglatMap.put("LAT", lat);
            ncellIdToLngLat.put(ncellId, lnglatMap);
        }
        //排序
        ncellIdToVal = sortMapByValue(ncellIdToVal);
        int num = 0;
        double toDistance = 0.0;
        for (String ncellId : ncellIdToVal.keySet()) {
            if (num >= 6) {
                break;
            }
            lnglatMap = ncellIdToLngLat.get(ncellId);
            double lng = lnglatMap.get("LNG");
            double lat = lnglatMap.get("LAT");
            if (lng == 0 || lat == 0) {
                continue;
            }
            toDistance += Math.sqrt((lng - cellLng) * (lng - cellLng) + (lat - cellLat) * (lat - cellLat));
            num++;
        }
        imgSize = toDistance / num;
        //如果图形大小系数未设置，取默认值
        if (imgSize == 0) {
            imgSize = DefaultImgSize;
        }
        List<ReferencePoint> points = new ArrayList<>();
        for (Map<String, Object> one : dynaCoverData) {
            double val = Double.parseDouble(one.get("VAL").toString()) * DefaultImgSizeCoff;
//			val =val;//乘以常量0.005
            double lng = Double.parseDouble(one.get("LNG").toString());
            double lat = Double.parseDouble(one.get("LAT").toString());
            if (lng == 0 || lat == 0) {
                continue;
            }
            double lngDiff = lng - cellLng;
            double latDiff = lat - cellLat;
            //正弦值
            double cosV = lngDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff));
            //余弦值
            double sinV = latDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff));
            double oneLng = cellLng + val * imgSize * cosV;
            double oneLat = cellLat + val * imgSize * sinV;

            points.add(new ReferencePoint(oneLng, oneLat));
        }
        long endTime = System.currentTimeMillis();
        log.debug("数据通过基准点转为百度坐标，耗时：" + (endTime - startTime));

        //以服务小区为中心，从第一象限到第四象限，按照角度大小排序
        startTime = System.currentTimeMillis();
        List<ReferencePoint> originPoints = ascAndInducePoints(points, cellLng, cellLat);
        endTime = System.currentTimeMillis();
        log.debug("数据坐标以服务小区为中心按照角度大小排序，耗时：" + (endTime - startTime));

        //计算矢量相加后的点坐标
        startTime = System.currentTimeMillis();
        double vectorLng = cellLng;
        double vectorLat = cellLat;
        for (ReferencePoint p : originPoints) {
            double lngDiff = p.getLng() - cellLng;
            double latDiff = p.getLat() - cellLat;
            vectorLng += lngDiff * EvalCoeff;
            vectorLat += latDiff * EvalCoeff;
        }
        List<Map<String, Object>> vectorPoints = new ArrayList<>();
        Map<String, Object> vectorPoint = new HashMap<>(3);
        vectorPoint.put("lng", vectorLng);
        vectorPoint.put("lat", vectorLat);
        vectorPoints.add(vectorPoint);
        result.put("vectorPoint", vectorPoints);

        //计算曲线的点集合
        List<Map<String, Object>> curvePoints = calculatePoints(originPoints);
        result.put("curvePoints", curvePoints);
        endTime = System.currentTimeMillis();
        log.debug("通过数据计算曲线点集合，耗时：" + (endTime - startTime));

        return result;
    }

    /**
     * 以服务小区为中心，从第一象限到第四象限，按照角度大小排序并归纳成8个点
     */
    private List<ReferencePoint> ascAndInducePoints(List<ReferencePoint> points,
                                                    double cellLng, double cellLat) {

        List<ReferencePoint> one = new ArrayList<>();
        List<ReferencePoint> two = new ArrayList<>();
        List<ReferencePoint> three = new ArrayList<>();
        List<ReferencePoint> four = new ArrayList<>();

        //划分落在每个象限的点
        for (ReferencePoint p : points) {
            //第一象限
            if (p.getLng() >= cellLng && p.getLat() > cellLat) {
                one.add(p);
            }
            //第二象限
            if (p.getLng() < cellLng && p.getLat() >= cellLat) {
                two.add(p);
            }
            //第三象限
            if (p.getLng() <= cellLng && p.getLat() < cellLat) {
                three.add(p);
            }
            //第四象限
            if (p.getLng() > cellLng && p.getLat() <= cellLat) {
                four.add(p);
            }
        }
        ReferencePoint temp = null;
        double xxi, yyi, xxj, yyj, sini, sinj;
        //将各自象限内的点按照角度划分顺序
        //第一象限
        for (int i = 0; i < one.size(); i++) {
            for (int j = i + 1; j < one.size(); j++) {
                xxi = one.get(i).getLng() - cellLng;
                yyi = one.get(i).getLat() - cellLat;
                xxj = one.get(j).getLng() - cellLng;
                yyj = one.get(j).getLat() - cellLat;
                sini = yyi / (Math.sqrt(xxi * xxi + yyi * yyi));
                sinj = yyj / (Math.sqrt(xxj * xxj + yyj * yyj));
                if (sini > sinj) {
                    temp = one.get(i);
                    one.set(i, one.get(j));
                    one.set(j, temp);
                }
            }
        }
        //第二象限
        for (int i = 0; i < two.size(); i++) {
            for (int j = i + 1; j < two.size(); j++) {
                xxi = two.get(i).getLng() - cellLng;
                yyi = two.get(i).getLat() - cellLat;
                xxj = two.get(j).getLng() - cellLng;
                yyj = two.get(j).getLat() - cellLat;
                sini = yyi / (Math.sqrt(xxi * xxi + yyi * yyi));
                sinj = yyj / (Math.sqrt(xxj * xxj + yyj * yyj));
                if (sini < sinj) {
                    temp = two.get(i);
                    two.set(i, two.get(j));
                    two.set(j, temp);
                }
            }
        }
        //第三象限
        for (int i = 0; i < three.size(); i++) {
            for (int j = i + 1; j < three.size(); j++) {
                xxi = three.get(i).getLng() - cellLng;
                yyi = three.get(i).getLat() - cellLat;
                xxj = three.get(j).getLng() - cellLng;
                yyj = three.get(j).getLat() - cellLat;
                sini = yyi / (Math.sqrt(xxi * xxi + yyi * yyi));
                sinj = yyj / (Math.sqrt(xxj * xxj + yyj * yyj));
                if (sini < sinj) {
                    temp = three.get(i);
                    three.set(i, three.get(j));
                    three.set(j, temp);
                }
            }
        }
        //第四象限
        for (int i = 0; i < four.size(); i++) {
            for (int j = i + 1; j < four.size(); j++) {
                xxi = four.get(i).getLng() - cellLng;
                yyi = four.get(i).getLat() - cellLat;
                xxj = four.get(j).getLng() - cellLng;
                yyj = four.get(j).getLat() - cellLat;
                sini = yyi / (Math.sqrt(xxi * xxi + yyi * yyi));
                sinj = yyj / (Math.sqrt(xxj * xxj + yyj * yyj));
                if (sini > sinj) {
                    temp = four.get(i);
                    four.set(i, four.get(j));
                    four.set(j, temp);
                }
            }
        }

        List<ReferencePoint> res = new ArrayList<>();
        //对每一象限分成两份，取每份的数据矢量相加作为一个参考点，即每个象限取两个参考点
        ReferencePoint tempPoint = null;
        double tempLng = cellLng;
        double tempLat = cellLat;
        for (int i = 0; i < one.size() / 2; i++) {
            tempLng += (one.get(i).getLng() - cellLng);
            tempLat += (one.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = one.size() / 2; i < one.size(); i++) {
            tempLng += (one.get(i).getLng() - cellLng);
            tempLat += (one.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        //第二象限
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = 0; i < two.size() / 2; i++) {
            tempLng += (two.get(i).getLng() - cellLng);
            tempLat += (two.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = two.size() / 2; i < two.size(); i++) {
            tempLng += (two.get(i).getLng() - cellLng);
            tempLat += (two.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        //第三象限
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = 0; i < three.size() / 2; i++) {
            tempLng += (three.get(i).getLng() - cellLng);
            tempLat += (three.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = three.size() / 2; i < three.size(); i++) {
            tempLng += (three.get(i).getLng() - cellLng);
            tempLat += (three.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        //第四象限
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = 0; i < four.size() / 2; i++) {
            tempLng += (four.get(i).getLng() - cellLng);
            tempLat += (four.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellLng;
        tempLat = cellLat;
        for (int i = four.size() / 2; i < four.size(); i++) {
            tempLng += (four.get(i).getLng() - cellLng);
            tempLat += (four.get(i).getLat() - cellLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);

        return res;
    }

    /**
     * 计算贝塞尔曲线的点集合
     */
    private List<Map<String, Object>> calculatePoints(
            List<ReferencePoint> originPoints) {
        int originCount = originPoints.size();
        ReferencePoint[] midPoints = new ReferencePoint[originCount];
        //生成中点
        for (int i = 0; i < originCount; i++) {
            int nexti = (i + 1) % originCount;
            double x = (originPoints.get(i).getLng() + originPoints.get(nexti).getLng()) / 2;
            double y = (originPoints.get(i).getLat() + originPoints.get(nexti).getLat()) / 2;
            midPoints[i] = new ReferencePoint(x, y);
        }
        //平移中点
        ReferencePoint[] extraPoints = new ReferencePoint[2 * originCount];
        for (int i = 0; i < originCount; i++) {
            int backi = (i + originCount - 1) % originCount;
            ReferencePoint midinmid = new ReferencePoint();
            midinmid.setLng((midPoints[i].getLng() + midPoints[backi].getLng()) / 2);
            midinmid.setLat((midPoints[i].getLat() + midPoints[backi].getLat()) / 2);
            double offsetx = originPoints.get(i).getLng() - midinmid.getLng();
            double offsety = originPoints.get(i).getLat() - midinmid.getLat();
            int extraindex = 2 * i;
            double x = midPoints[backi].getLng() + offsetx;
            double y = midPoints[backi].getLat() + offsety;
            extraPoints[extraindex] = new ReferencePoint(x, y);
            //朝 originPoint[i]方向收缩
            double addx = (extraPoints[extraindex].getLng() - originPoints.get(i).getLng()) * Scale;
            double addy = (extraPoints[extraindex].getLat() - originPoints.get(i).getLat()) * Scale;
            extraPoints[extraindex].setLng(originPoints.get(i).getLng() + addx);
            extraPoints[extraindex].setLat(originPoints.get(i).getLat() + addy);

            int extranexti = (extraindex + 1) % (2 * originCount);
            x = midPoints[i].getLng() + offsetx;
            y = midPoints[i].getLat() + offsety;
            extraPoints[extranexti] = new ReferencePoint(x, y);
            //朝 originPoint[i]方向收缩
            addx = (extraPoints[extranexti].getLng() - originPoints.get(i).getLng()) * Scale;
            addy = (extraPoints[extranexti].getLat() - originPoints.get(i).getLat()) * Scale;
            extraPoints[extranexti].setLng(originPoints.get(i).getLng() + addx);
            extraPoints[extranexti].setLat(originPoints.get(i).getLat() + addy);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> tempP = null;
        ReferencePoint[] controlPoint = new ReferencePoint[4];
        //生成4控制点，产生贝塞尔曲线
        for (int i = 0; i < originCount; i++) {
            controlPoint[0] = originPoints.get(i);
            int extraindex = 2 * i;
            controlPoint[1] = extraPoints[extraindex + 1];
            int extranexti = (extraindex + 2) % (2 * originCount);
            controlPoint[2] = extraPoints[extranexti];
            int nexti = (i + 1) % originCount;
            controlPoint[3] = originPoints.get(nexti);
            float u = 1;
            while (u >= 0) {
                //计算贝塞尔曲线
                double px = bezier3funcX(u, controlPoint);
                double py = bezier3funcY(u, controlPoint);
                //u的步长决定曲线的疏密
                u -= Step;
                tempP = new HashMap<>();
                tempP.put("lng", px);
                tempP.put("lat", py);
                //存入曲线点
                result.add(tempP);
            }
        }
        return result;
    }

    private double bezier3funcX(float uu, ReferencePoint[] controlPoint) {
        double part0 = controlPoint[0].getLng() * uu * uu * uu;
        double part1 = 3 * controlPoint[1].getLng() * uu * uu * (1 - uu);
        double part2 = 3 * controlPoint[2].getLng() * uu * (1 - uu) * (1 - uu);
        double part3 = controlPoint[3].getLng() * (1 - uu) * (1 - uu) * (1 - uu);
        return part0 + part1 + part2 + part3;
    }

    private double bezier3funcY(float uu, ReferencePoint[] controlPoint) {
        double part0 = controlPoint[0].getLat() * uu * uu * uu;
        double part1 = 3 * controlPoint[1].getLat() * uu * uu * (1 - uu);
        double part2 = 3 * controlPoint[2].getLat() * uu * (1 - uu) * (1 - uu);
        double part3 = controlPoint[3].getLat() * (1 - uu) * (1 - uu) * (1 - uu);
        return part0 + part1 + part2 + part3;
    }

    /**
     * 检查小区是华为还是爱立信
     */
    private String checkCellIsHwOrEri(final String enName) {
        log.debug("enName ===={}", enName);
        Map<String, Object> map = new HashMap<>();
        map.put("enName", enName);
        List<Map<String, Object>> cellDataList = gsmDynamicCoverageMapper.selectManufacturersFromRnoBsc(map);
        String result = null;
        if (cellDataList.size() > 0) {
            result = cellDataList.get(0).get("DESC_ID").toString();
        }
        return result;
    }

    /**
     * 查询爱立信ncs数据，并整理得到需要结果
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryEriDataFromOracle(
            final long cityId, String cellId, final String startDate, final String endDate, String relss) {

        String startTime = String.format(startDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        String endTime = String.format(endDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", "'" + cityId + "'");
        map.put("startTime", "'" + startTime + "'");
        map.put("endTime", "'" + endTime + "'");
        List<Map<String, Object>> eriNcsDescInfos = gsmDynamicCoverageMapper.selectFromRnoGSMEriNcsDescripter(map);
        if (eriNcsDescInfos.size() <= 0) {
            return null;
        }
        String ncsFields = "CELL,NCELL,CELL_LON,CELL_LAT,NCELL_LON,NCELL_LAT,REPARFCN,TIMESRELSS,TIMESRELSS2,TIMESRELSS3,TIMESRELSS4,TIMESRELSS5,DISTANCE,INTERFER";
        String timesRelss = "";
        String ncsDescId = "";
        Map<String, Object> ncellMap = new HashMap<>();
        ncellMap.put("cellId", "'" + cellId + "'");
        ncellMap.put("areaId", cityId);
        List<Map<String, Object>> gisCells = gsmDynamicCoverageMapper.getNcellDetailsByCellandCityId(ncellMap);
        String areaIdStr = "";
        for (Map<String, Object> oneCell : gisCells) {
            areaIdStr += oneCell.get("AREA_ID") + ",";
        }
        if (areaIdStr.length() > 0) {
            areaIdStr = areaIdStr.substring(0, areaIdStr.length() - 1);
        }
        //先清空临时表
        gsmDynamicCoverageMapper.deleteAll();

        Map<String, Object> insertMaps = new HashMap<>();
        insertMaps.put("ncsFields", ncsFields);
        insertMaps.put("cellId", "'" + cellId + "'");
        insertMaps.put("areaIdStr", areaIdStr);
        insertMaps.put("cityId", cityId);
        insertMaps.put("startTime", "'" + startTime + "'");
        insertMaps.put("endTime", "'" + endTime + "'");
        gsmDynamicCoverageMapper.insertIntoRno2GNcsCoverT(insertMaps);

        for (Map<String, Object> ncsDesc : eriNcsDescInfos) {

            ncsDescId = ncsDesc.get("DESC_ID").toString();
            //确定门限值字段
            timesRelss = getEriTimesRelssXByValue(ncsDesc, relss);
            if (timesRelss == null || "".equals(timesRelss)) {
                log.debug("动态覆盖图：ncs[" + ncsDescId + "]未获取到相应的[" + relss + "]对应的列，将尝试用+0获取");
                timesRelss = getEriTimesRelssXByValue(ncsDesc, "+0");
                log.debug("动态覆盖图：获取[+0]对应的列为：" + timesRelss);
            }
            if ("".equals(timesRelss)) {
                continue;
            }
            Map<String, Object> updateMaps = new HashMap<>();
            updateMaps.put("TIMESRELSS", timesRelss);
            updateMaps.put("ncsDescId", ncsDescId);
            updateMaps.put("cellId", "'" + cellId + "'");
            updateMaps.put("areaIdStr", areaIdStr);
            gsmDynamicCoverageMapper.updateRno2GNcsCoverTSetInterfer(updateMaps);
        }
        List<Map<String, Object>> rss = gsmDynamicCoverageMapper.selectInterferCelllonCelllatCellNcelllonNcelllatFromRno2GNcsCoverT();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> oneRss : rss) {
            map = new HashMap<>();
            map.put("VAL", oneRss.get("VAL"));
            map.put("LNG", oneRss.get("LNG"));
            map.put("LAT", oneRss.get("LAT"));
            map.put("NCELL_ID", oneRss.get("NCELL_ID"));
            map.put("CELL_LNG", oneRss.get("CELL_LNG"));
            map.put("CELL_LAT", oneRss.get("CELL_LAT"));
            result.add(map);
        }
        gsmDynamicCoverageMapper.deleteAll();
        return result;
    }

    /**
     * 根据限值获取爱立信ncs中对应的timesrelss
     *
     * @param desc
     * @param relsscons
     * @return
     */
    private String getEriTimesRelssXByValue(Map<String, Object> desc,
                                            String relsscons) {
        String timesRelss = "";
        String relss;
        relss = (Integer.parseInt(desc.get("RELSS_SIGN").toString()) == 0 ? "+" : "-")
                + desc.get("RELSS").toString();
        if (relsscons.equals(relss)) {
            timesRelss = "TIMESRELSS";
        } else {
            relss = (Integer.parseInt(desc.get("RELSS2_SIGN").toString()) == 0 ? "+" : "-")
                    + desc.get("RELSS2").toString();
            if (relsscons.equals(relss)) {
                timesRelss = "TIMESRELSS2";
            } else {
                relss = (Integer.parseInt(desc.get("RELSS3_SIGN").toString()) == 0 ? "+" : "-")
                        + desc.get("RELSS3").toString();
                if (relsscons.equals(relss)) {
                    timesRelss = "TIMESRELSS3";
                } else {
                    relss = (Integer.parseInt(desc.get("RELSS4_SIGN").toString()) == 0 ? "+" : "-")
                            + desc.get("RELSS4").toString();
                    if (relsscons.equals(relss)) {
                        timesRelss = "TIMESRELSS4";
                    } else {
                        relss = (Integer.parseInt(desc.get("RELSS5_SIGN").toString()) == 0 ? "+" : "-")
                                + desc.get("RELSS5").toString();
                        if (relsscons.equals(relss)) {
                            timesRelss = "TIMESRELSS5";
                        }
                    }
                }
            }
        }
        return timesRelss;
    }

    /**
     * 查询华为ncs数据，并整理得到需要结果
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryHwDataFromOracle(final long cityId,
                                                            final String cell, String cellId, final String startDate, final String endDate, final String relss) {

        String startTime = String.format(startDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        String endTime = String.format(endDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        Map<String, Object> maps = new HashMap<>();
        maps.put("cityId", "'" + cityId + "'");
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);
        List<Map<String, Object>> hwNcsDescInfos = gsmDynamicCoverageMapper.selectIdFromRno2GHwNcsDesc(maps);

        Map<String, Object> ncellMap = new HashMap<>();
        ncellMap.put("cellId", "'" + cellId + "'");
        ncellMap.put("areaId", cityId);
        List<Map<String, Object>> gisCells = gsmDynamicCoverageMapper.getNcellDetailsByCellandCityId(ncellMap);
        StringBuilder areaIdStringBuilder = new StringBuilder();
        for (Map<String, Object> oneCell : gisCells) {
            areaIdStringBuilder.append(oneCell.get("AREA_ID")).append(",");
        }
        String areaIdStr = "";
        if (areaIdStringBuilder.toString().length() > 0) {
            areaIdStr = areaIdStringBuilder.toString().substring(0, areaIdStringBuilder.toString().length() - 1);
        }
        StringBuilder descIdStr = new StringBuilder();
        for (Map<String, Object> map : hwNcsDescInfos) {
            descIdStr.append(map.get("DESC_ID").toString()).append(",");
        }
        if (("").equals(descIdStr.toString())) {
            log.debug("cityId=" + cityId + ",cell=" + cell + ",startDate="
                    + startDate + ",endDate=" + endDate + ",找不到对应的华为ncs描述信息！");
            return Collections.emptyList();
        }
        final String descIds = descIdStr.substring(0, descIdStr.length() - 1);

        String valStr = "";
        if ("-12".equals(relss)) {
            valStr = "(s361-s369)/s3013 as val,";
        } else {
            valStr = "(s361-s366)/s3013 as val,";
        }
        maps = new HashMap<>();
        maps.put("valStr", valStr);
        maps.put("cell", cell);
        maps.put("descIds", descIds);
        maps.put("areaIdStr", areaIdStr);
        return gsmDynamicCoverageMapper.selectValstrCelllonCelllatCellNcelllonNcelllatFrom(maps);
    }
}
