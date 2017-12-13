package com.hgicreate.rno.service.gsm;


import com.hgicreate.rno.domain.gsm.AreaRectangle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RnoNcsDynaCoverageServiceImpl implements RnoNcsDynaCoverageService {
    private static Log log = LogFactory.getLog(RnoNcsDynaCoverageServiceImpl.class);

    private static double defaultImgSize = 0.005; // 曲线点距离服务小区的长度系数0.005
    private static double evalCoeff = 0.3; // 服务小区评估方向的长度系数，经调试0.3较好
    private static float scale = 0.6f; //控制点收缩系数 ，经调试0.6较好
    private static double step = 0.01; //曲线密度(0-1)0.01
    private static double defaultImgSizeCoff = 10; // 矢量长度系数10
    @Autowired
    private final RnoNcsDynaCoverageDao rnoNcsDynaCoverageDao;

    public RnoNcsDynaCoverageServiceImpl(RnoNcsDynaCoverageDao rnoNcsDynaCoverageDao) {
        this.rnoNcsDynaCoverageDao = rnoNcsDynaCoverageDao;
    }

    /**
     * 获取画小区动态覆盖图所需的数据
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    @Override
    public Map<String, List<Map<String, Object>>> getDynaCoverageDataByCityAndDate(
            String cell, long cityId, String startDate, String endDate,
            Map<AreaRectangle, List<Map<String, Object>>> standardPoints) {

        log.debug("getDynaCoverageDataByCityAndDate, cell=" + cell + ", cityId=" + cityId
                + ", startDate=" + startDate + ", endDate=" + endDate);

        long stime = System.currentTimeMillis();
        //先判断小区是华为小区还是爱立信小区
        String sCell = "'" + cell + "'";
        String manu = rnoNcsDynaCoverageDao.checkCellIsHwOrEri(sCell);
        if (manu == null) {
            log.error("不能判断该小区是哪个厂家！ cell=" + cell);
            return null;
        }
        //RELSS>-12
        List<Map<String, Object>> dynaCoverData_12 = new ArrayList<Map<String, Object>>();
        //RELSS>3
        List<Map<String, Object>> dynaCoverData_3 = new ArrayList<Map<String, Object>>();

        if (("1").equals(manu)) {
            //爱立信小区
            dynaCoverData_12 = rnoNcsDynaCoverageDao
                    .queryEriDataFromOracle(cityId, cell, startDate, endDate, "-12");
            dynaCoverData_3 = rnoNcsDynaCoverageDao
                    .queryEriDataFromOracle(cityId, cell, startDate, endDate, "+3");
        } else if (("2").equals(manu)) {
            //华为小区
            dynaCoverData_12 = rnoNcsDynaCoverageDao
                    .queryHwDataFromOracle(cityId, cell, startDate, endDate, "-12");
            dynaCoverData_3 = rnoNcsDynaCoverageDao
                    .queryHwDataFromOracle(cityId, cell, startDate, endDate, "+3");
        }
        if ((dynaCoverData_12 == null || dynaCoverData_12.size() == 0)
                && (dynaCoverData_3 == null || dynaCoverData_3.size() == 0)) {
            return null;
        }
        long etime = System.currentTimeMillis();
        log.debug("从数据库读取(RELSS>-12)数据量：" + dynaCoverData_12.size()
                + ";(RELSS>3)数据量：" + dynaCoverData_3.size() + "; 共耗时：" + (etime - stime));

        //获取画(RELSS>-12)图形所需曲线点坐标集合
        Map<String, List<Map<String, Object>>> res_12
                = calcDynaCoveragePointsData(dynaCoverData_12, standardPoints);
        //获取画(RELSS>3)图形所需曲线点坐标集合
        Map<String, List<Map<String, Object>>> res_3
                = calcDynaCoveragePointsData(dynaCoverData_3, standardPoints);

        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        result.put("vectorPoint_12", res_12.get("vectorPoint"));
        result.put("vectorPoint_3", res_3.get("vectorPoint"));
        result.put("curvePoints_12", res_12.get("curvePoints"));
        result.put("curvePoints_3", res_3.get("curvePoints"));
        return result;
    }


    /**
     * 获取画小区动态覆盖图(折线)所需的数据
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年4月30日9:55:32
     * @company 怡创科技
     */
    @Override
    public Map<String, List<Map<String, Object>>> getDynaCoverageData2ByCityAndDate(
            String cell, long cityId, String startDate, String endDate,
            double imgCoeff,
            Map<AreaRectangle, List<Map<String, Object>>> standardPoints) {
        log.debug("getDynaCoverageData2ByCityAndDate, cell=" + cell + ", cityId=" + cityId
                + ", startDate=" + startDate + ", endDate=" + endDate);

        long stime = System.currentTimeMillis();
        //先判断小区是华为小区还是爱立信小区
        String sCell = "'" + cell + "'";
        String manu = rnoNcsDynaCoverageDao.checkCellIsHwOrEri(sCell);
        if (manu == null) {
            log.error("不能判断该小区是哪个厂家！ cell=" + cell);
            return null;
        }
        //RELSS>-12
        List<Map<String, Object>> dynaCoverData_12 = new ArrayList<Map<String, Object>>();
        //RELSS>3
        List<Map<String, Object>> dynaCoverData_3 = new ArrayList<Map<String, Object>>();

        if (("1").equals(manu)) {
            //爱立信小区
            dynaCoverData_12 = rnoNcsDynaCoverageDao
                    .queryEriDataFromOracle(cityId, cell, startDate, endDate, "-12");
            dynaCoverData_3 = rnoNcsDynaCoverageDao
                    .queryEriDataFromOracle(cityId, cell, startDate, endDate, "+3");
        } else if (("2").equals(manu)) {
            //华为小区
            dynaCoverData_12 = rnoNcsDynaCoverageDao
                    .queryHwDataFromOracle(cityId, cell, startDate, endDate, "-12");
            dynaCoverData_3 = rnoNcsDynaCoverageDao
                    .queryHwDataFromOracle(cityId, cell, startDate, endDate, "+3");
        }
        if ((dynaCoverData_12 == null || dynaCoverData_12.size() == 0)
                && (dynaCoverData_3 == null || dynaCoverData_3.size() == 0)) {
            return null;
        }
        long etime = System.currentTimeMillis();
        log.debug("从数据库读取(RELSS>-12)数据量：" + dynaCoverData_12.size()
                + ";(RELSS>3)数据量：" + dynaCoverData_3.size() + "; 共耗时：" + (etime - stime));

        //获取画(RELSS>-12)图形所需折线点坐标集合
        Map<String, List<Map<String, Object>>> res_12
                = calcDynaCoveragePointsData2(dynaCoverData_12, imgCoeff, standardPoints);
        //获取画(RELSS>3)图形所需折线点坐标集合
        Map<String, List<Map<String, Object>>> res_3
                = calcDynaCoveragePointsData2(dynaCoverData_3, imgCoeff, standardPoints);

        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        result.put("vectorPoint_12", res_12.get("vectorPoint"));
        result.put("vectorPoint_3", res_3.get("vectorPoint"));
        result.put("curvePoints_12", res_12.get("curvePoints"));
        result.put("curvePoints_3", res_3.get("curvePoints"));
        return result;
    }


    /**
     * 获取画图所需折线点坐标集合
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    private Map<String, List<Map<String, Object>>> calcDynaCoveragePointsData2(
            List<Map<String, Object>> dynaCoverData,
            double imgCoeff,
            Map<AreaRectangle, List<Map<String, Object>>> standardPoints) {

        long stime = System.currentTimeMillis();

        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();

        double cellLng = Double.parseDouble(dynaCoverData.get(0).get("CELL_LNG").toString());
        double cellLat = Double.parseDouble(dynaCoverData.get(0).get("CELL_LAT").toString());


        //图形大小系数取最强前六邻区与本小区的距离平均值
        double imgSize = 0.0;
        //取出最强6邻区的相关数据，用于获取距离平均值
        Map<String, Map<String, Double>> ncellIdToLngLat = new HashMap<String, Map<String, Double>>();
        Map<String, Double> lnglatMap = null;
        Map<String, Double> ncellIdToVal = new HashMap<String, Double>();
        for (Map<String, Object> one : dynaCoverData) {
            double val = Double.parseDouble(one.get("VAL").toString());
            double lng = Double.parseDouble(one.get("LNG").toString());
            double lat = Double.parseDouble(one.get("LAT").toString());
            String ncellId = one.get("NCELL_ID").toString();
            ncellIdToVal.put(ncellId, val);
            lnglatMap = new HashMap<String, Double>();
            lnglatMap.put("LNG", lng);
            lnglatMap.put("LAT", lat);
            ncellIdToLngLat.put(ncellId, lnglatMap);
        }
        //排序
        ncellIdToVal = sortMapByValue(ncellIdToVal);
        int num = 0;
        double totDistance = 0.0;
        for (String ncellId : ncellIdToVal.keySet()) {
            if (num >= 6) break;//迭加前6强距离
//			if(num >= 1) break;//取最大值
            lnglatMap = ncellIdToLngLat.get(ncellId);
            double lng = lnglatMap.get("LNG");
            double lat = lnglatMap.get("LAT");
            if (lng == 0 || lat == 0) continue;
            totDistance += Math.sqrt((lng - cellLng) * (lng - cellLng) + (lat - cellLat) * (lat - cellLat));
            num++;
        }
        imgSize = totDistance / num;

        //如果图形大小系数未设置，取默认值
        if (imgSize == 0) {
            imgSize = defaultImgSize;
        }

        List<ReferencePoint> points = new ArrayList<ReferencePoint>();
        ReferencePoint point = null;
        for (Map<String, Object> one : dynaCoverData) {
            double val = Double.parseDouble(one.get("VAL").toString());
            val = val;//乘以常量10
            double lng = Double.parseDouble(one.get("LNG").toString());
            double lat = Double.parseDouble(one.get("LAT").toString());

            if (lng == 0 || lat == 0) {
                continue;
            }

            //转化成百度坐标
//            String baiduPoint[] = getBaiduLnglat(lng, lat, standardPoints);
//            if (baiduPoint == null) {
//                continue;
//            }
//            double oneBaiduLng = lng;
//            double oneBaiduLat = lat;

            double lngDiff = lng - cellLng;
            double latDiff = lat - cellLat;
            double cosV = lngDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff)); //正弦值
            double sinV = latDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff)); //余弦值

            double oneLng = cellLng + val * imgSize * cosV;
            double oneLat = cellLat + val * imgSize * sinV;

            point = new ReferencePoint(oneLng, oneLat);
            points.add(point);
        }
        long etime = System.currentTimeMillis();
        log.debug("数据通过基准点转为百度坐标，耗时：" + (etime - stime));

        //以服务小区为中心，从第一象限到第四象限，按照角度大小排序
        //归纳16个点
        List<ReferencePoint> originPoints = ascAndInduce16Points(points, cellLng, cellLat);
        //归纳8个点
        //List<ReferencePoint> originPoints = ascAndInducePoints(points,cellbaiduLng,cellbaiduLat);

        //计算矢量相加后的点坐标
        //stime = System.currentTimeMillis();
        double vectorlng = cellLng;
        double vectorlat = cellLat;

        for (ReferencePoint p : originPoints) {
            double lngdiff = p.getBaiduLng() - cellLng;
            double latdiff = p.getBaiduLat() - cellLat;
            vectorlng += lngdiff * evalCoeff;
            vectorlat += latdiff * evalCoeff;
        }
        List<Map<String, Object>> vectorPoints = new ArrayList<Map<String, Object>>();
        Map<String, Object> vectorPoint = new HashMap<String, Object>();
        vectorPoint.put("lng", vectorlng);
        vectorPoint.put("lat", vectorlat);
        vectorPoints.add(vectorPoint);
        result.put("vectorPoint", vectorPoints);

        //计算折线的点集合
        List<Map<String, Object>> curvePoints = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        //保证imgCoeff不为0
        if (imgCoeff == 0) {
            imgCoeff = 0.5;
        }
        //加入中点
        for (int i = 0; i < originPoints.size(); i++) {
            int nexti = (i + 1) % originPoints.size();
            double preLng = originPoints.get(i).getBaiduLng();
            double preLat = originPoints.get(i).getBaiduLat();
            double nextLng = originPoints.get(nexti).getBaiduLng();
            double nextLat = originPoints.get(nexti).getBaiduLat();

            double midLng = imgCoeff * cellLng;
            double midLat = imgCoeff * cellLat;
            //中点
            map = new HashMap<String, Object>();
            map.put("lng", midLng);
            map.put("lat", midLat);
            curvePoints.add(map);
            //next点
            map = new HashMap<String, Object>();
            map.put("lng", nextLng);
            map.put("lat", nextLat);
            curvePoints.add(map);
        }

        result.put("curvePoints", curvePoints);
        etime = System.currentTimeMillis();
        log.debug("通过数据计算折线点集合，耗时：" + (etime - stime));

        return result;
    }

    class ReferencePoint {
        double baiduLng;
        double baiduLat;

        public ReferencePoint() {
        }

        public ReferencePoint(double baiduLng, double baiduLat) {
            this.baiduLng = baiduLng;
            this.baiduLat = baiduLat;
        }

        public double getBaiduLng() {
            return baiduLng;
        }

        public void setBaiduLng(double baiduLng) {
            this.baiduLng = baiduLng;
        }

        public double getBaiduLat() {
            return baiduLat;
        }

        public void setBaiduLat(double baiduLat) {
            this.baiduLat = baiduLat;
        }

        @Override
        public String toString() {
            return "ReferencePoint [baiduLng=" + baiduLng + ", baiduLat="
                    + baiduLat + "]";
        }

    }

    /**
     * map根据value值,从大到小排序
     *
     * @param unsortMap
     * @return
     * @author peng.jm
     */
    public Map sortMapByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * 获取百度坐标
     *
     * @param longitude
     * @param latitude
     * @param standardPoints
     * @return
     */
    private String[] getBaiduLnglat(double longitude, double latitude,
                                    Map<AreaRectangle, List<Map<String, Object>>> standardPoints) {

//        String[] baidulnglat = null;
//        if (baidulnglat == null) {
//            if (standardPoints != null && standardPoints.size() > 0) {
//            } else {
//                log.info("区域不存在基准点，将使用百度在线接口进行校正。");
//            }
//        }

        String[] newlnglat = new String[2];
        newlnglat[0] = longitude + "";
        newlnglat[1] = latitude + "";
        return newlnglat;
    }

    /**
     * 获取画图所需曲线点坐标集合
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    private Map<String, List<Map<String, Object>>> calcDynaCoveragePointsData(
            List<Map<String, Object>> dynaCoverData,
            Map<AreaRectangle, List<Map<String, Object>>> standardPoints) {

        long stime = System.currentTimeMillis();

        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();

        double cellLng = Double.parseDouble(dynaCoverData.get(0).get("CELL_LNG").toString());
        double cellLat = Double.parseDouble(dynaCoverData.get(0).get("CELL_LAT").toString());

        //图形大小系数取最强前六邻区与本小区的距离平均值
        double imgSize = 0.0;
        //取出最强6邻区的相关数据，用于获取距离平均值
        Map<String, Map<String, Double>> ncellIdToLngLat = new HashMap<String, Map<String, Double>>();
        Map<String, Double> lnglatMap = null;
        Map<String, Double> ncellIdToVal = new HashMap<String, Double>();
        for (Map<String, Object> one : dynaCoverData) {
            double val = Double.parseDouble(one.get("VAL").toString());
            double lng = Double.parseDouble(one.get("LNG").toString());
            double lat = Double.parseDouble(one.get("LAT").toString());
            String ncellId = one.get("NCELL_ID").toString();
            ncellIdToVal.put(ncellId, val);
            lnglatMap = new HashMap<String, Double>();
            lnglatMap.put("LNG", lng);
            lnglatMap.put("LAT", lat);
            ncellIdToLngLat.put(ncellId, lnglatMap);
        }
        //排序
        ncellIdToVal = sortMapByValue(ncellIdToVal);
        int num = 0;
        double totDistance = 0.0;
        for (String ncellId : ncellIdToVal.keySet()) {
            if (num >= 6) break;
            lnglatMap = ncellIdToLngLat.get(ncellId);
            double lng = lnglatMap.get("LNG");
            double lat = lnglatMap.get("LAT");
            if (lng == 0 || lat == 0) continue;
            totDistance += Math.sqrt((lng - cellLng) * (lng - cellLng) + (lat - cellLat) * (lat - cellLat));
            num++;
        }
        imgSize = totDistance / num;

        //如果图形大小系数未设置，取默认值
        if (imgSize == 0) {
            imgSize = defaultImgSize;
        }


        String cellBaiduPoint[] = getBaiduLnglat(cellLng, cellLat, standardPoints);
        if (cellBaiduPoint == null) {
            return null;
        }
        double cellbaiduLng = Double.parseDouble(cellBaiduPoint[0]);
        double cellbaiduLat = Double.parseDouble(cellBaiduPoint[1]);

        List<ReferencePoint> points = new ArrayList<ReferencePoint>();
        ReferencePoint point = null;
        for (Map<String, Object> one : dynaCoverData) {
            double val = Double.parseDouble(one.get("VAL").toString()) * 8;
//			val =val;//乘以常量0.005
            double lng = Double.parseDouble(one.get("LNG").toString());
            double lat = Double.parseDouble(one.get("LAT").toString());

            if (lng == 0 || lat == 0) {
                continue;
            }

            //转化成百度坐标
            String baiduPoint[] = getBaiduLnglat(lng, lat, standardPoints);
            if (baiduPoint == null) {
                continue;
            }
            double oneBaiduLng = Double.parseDouble(baiduPoint[0]);
            double oneBaiduLat = Double.parseDouble(baiduPoint[1]);

            double lngDiff = oneBaiduLng - cellbaiduLng;
            double latDiff = oneBaiduLat - cellbaiduLat;
            double cosV = lngDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff)); //正弦值
            double sinV = latDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff)); //余弦值

            double oneLng = cellbaiduLng + val * imgSize * cosV;
            double oneLat = cellbaiduLat + val * imgSize * sinV;
            point = new ReferencePoint(oneLng, oneLat);
            points.add(point);
        }
        long etime = System.currentTimeMillis();
        log.debug("数据通过基准点转为百度坐标，耗时：" + (etime - stime));

        //以服务小区为中心，从第一象限到第四象限，按照角度大小排序
        stime = System.currentTimeMillis();
        List<ReferencePoint> originPoints = ascAndInducePoints(points, cellbaiduLng, cellbaiduLat);
        etime = System.currentTimeMillis();
        log.debug("数据坐标以服务小区为中心按照角度大小排序，耗时：" + (etime - stime));

        //计算矢量相加后的点坐标
        stime = System.currentTimeMillis();
        double vectorlng = cellbaiduLng;
        double vectorlat = cellbaiduLat;
        for (ReferencePoint p : originPoints) {
            double lngdiff = p.getBaiduLng() - cellbaiduLng;
            double latdiff = p.getBaiduLat() - cellbaiduLat;
            vectorlng += lngdiff * evalCoeff;
            vectorlat += latdiff * evalCoeff;
        }
        List<Map<String, Object>> vectorPoints = new ArrayList<Map<String, Object>>();
        Map<String, Object> vectorPoint = new HashMap<String, Object>();
        vectorPoint.put("lng", vectorlng);
        vectorPoint.put("lat", vectorlat);
        vectorPoints.add(vectorPoint);
        result.put("vectorPoint", vectorPoints);

        //计算曲线的点集合
        List<Map<String, Object>> curvePoints = calculatePoints(originPoints);
        result.put("curvePoints", curvePoints);
        etime = System.currentTimeMillis();
        log.debug("通过数据计算曲线点集合，耗时：" + (etime - stime));

        return result;
    }

    /**
     * 以服务小区为中心，从第一象限到第四象限，按照角度大小排序并归纳成8个点
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    private List<ReferencePoint> ascAndInducePoints(List<ReferencePoint> points,
                                                    double cellbaiduLng, double cellbaiduLat) {

        List<ReferencePoint> one = new ArrayList<ReferencePoint>();
        List<ReferencePoint> two = new ArrayList<ReferencePoint>();
        List<ReferencePoint> three = new ArrayList<ReferencePoint>();
        List<ReferencePoint> four = new ArrayList<ReferencePoint>();

        //划分落在每个象限的点

        for (ReferencePoint p : points) {
            //第一象限
            if (p.getBaiduLng() >= cellbaiduLng && p.getBaiduLat() > cellbaiduLat) {
                one.add(p);
            }
            //第二象限
            if (p.getBaiduLng() < cellbaiduLng && p.getBaiduLat() >= cellbaiduLat) {
                two.add(p);
            }
            //第三象限
            if (p.getBaiduLng() <= cellbaiduLng && p.getBaiduLat() < cellbaiduLat) {
                three.add(p);
            }
            //第四象限
            if (p.getBaiduLng() > cellbaiduLng && p.getBaiduLat() <= cellbaiduLat) {
                four.add(p);
            }
        }

        ReferencePoint temp = null;
        double xxi, yyi, xxj, yyj, sini, sinj;
        //将各自象限内的点按照角度划分顺序
        //第一象限
        for (int i = 0; i < one.size(); i++) {
            for (int j = i + 1; j < one.size(); j++) {
                xxi = one.get(i).getBaiduLng() - cellbaiduLng;
                yyi = one.get(i).getBaiduLat() - cellbaiduLat;
                xxj = one.get(j).getBaiduLng() - cellbaiduLng;
                yyj = one.get(j).getBaiduLat() - cellbaiduLat;
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
                xxi = two.get(i).getBaiduLng() - cellbaiduLng;
                yyi = two.get(i).getBaiduLat() - cellbaiduLat;
                xxj = two.get(j).getBaiduLng() - cellbaiduLng;
                yyj = two.get(j).getBaiduLat() - cellbaiduLat;
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
                xxi = three.get(i).getBaiduLng() - cellbaiduLng;
                yyi = three.get(i).getBaiduLat() - cellbaiduLat;
                xxj = three.get(j).getBaiduLng() - cellbaiduLng;
                yyj = three.get(j).getBaiduLat() - cellbaiduLat;
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
                xxi = four.get(i).getBaiduLng() - cellbaiduLng;
                yyi = four.get(i).getBaiduLat() - cellbaiduLat;
                xxj = four.get(j).getBaiduLng() - cellbaiduLng;
                yyj = four.get(j).getBaiduLat() - cellbaiduLat;
                sini = yyi / (Math.sqrt(xxi * xxi + yyi * yyi));
                sinj = yyj / (Math.sqrt(xxj * xxj + yyj * yyj));
                if (sini > sinj) {
                    temp = four.get(i);
                    four.set(i, four.get(j));
                    four.set(j, temp);
                }
            }
        }

        List<ReferencePoint> res = new ArrayList<ReferencePoint>();
        //int oneNum=0,twoNum=0,threeNum=0,fourNum=0;

        //对每一象限分成两份，取每份的数据矢量相加作为一个参考点，即每个象限取两个参考点
        ReferencePoint tempPoint = null;
        double tempLng = cellbaiduLng;
        double tempLat = cellbaiduLat;
        for (int i = 0; i < one.size() / 2; i++) {
            tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = one.size() / 2; i < one.size(); i++) {
            tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        //第二象限
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = 0; i < two.size() / 2; i++) {
            tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = two.size() / 2; i < two.size(); i++) {
            tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        //第三象限
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = 0; i < three.size() / 2; i++) {
            tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = three.size() / 2; i < three.size(); i++) {
            tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        //第四象限
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = 0; i < four.size() / 2; i++) {
            tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);
        tempLng = cellbaiduLng;
        tempLat = cellbaiduLat;
        for (int i = four.size() / 2; i < four.size(); i++) {
            tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
            tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
        }
        tempPoint = new ReferencePoint(tempLng, tempLat);
        res.add(tempPoint);

        return res;
    }

    /**
     * 计算贝塞尔曲线的点集合
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    private List<Map<String, Object>> calculatePoints(
            List<ReferencePoint> originPoints) {


        int originCount = originPoints.size();
        ReferencePoint midpoints[] = new ReferencePoint[originCount];
        //生成中点
        for (int i = 0; i < originCount; i++) {
            int nexti = (i + 1) % originCount;
            double x = (originPoints.get(i).getBaiduLng() + originPoints.get(nexti).getBaiduLng()) / 2;
            double y = (originPoints.get(i).getBaiduLat() + originPoints.get(nexti).getBaiduLat()) / 2;
            midpoints[i] = new ReferencePoint(x, y);
        }

        //平移中点
        ReferencePoint extrapoints[] = new ReferencePoint[2 * originCount];
        for (int i = 0; i < originCount; i++) {
            //int nexti = (i + 1) % originCount;
            int backi = (i + originCount - 1) % originCount;
            ReferencePoint midinmid = new ReferencePoint();
            midinmid.setBaiduLng((midpoints[i].getBaiduLng() + midpoints[backi].getBaiduLng()) / 2);
            midinmid.setBaiduLat((midpoints[i].getBaiduLat() + midpoints[backi].getBaiduLat()) / 2);
            double offsetx = originPoints.get(i).getBaiduLng() - midinmid.getBaiduLng();
            double offsety = originPoints.get(i).getBaiduLat() - midinmid.getBaiduLat();
            int extraindex = 2 * i;
            double x = midpoints[backi].getBaiduLng() + offsetx;
            double y = midpoints[backi].getBaiduLat() + offsety;
            extrapoints[extraindex] = new ReferencePoint(x, y);
            //朝 originPoint[i]方向收缩
            double addx = (extrapoints[extraindex].getBaiduLng() - originPoints.get(i).getBaiduLng()) * scale;
            double addy = (extrapoints[extraindex].getBaiduLat() - originPoints.get(i).getBaiduLat()) * scale;
            extrapoints[extraindex].setBaiduLng(originPoints.get(i).getBaiduLng() + addx);
            extrapoints[extraindex].setBaiduLat(originPoints.get(i).getBaiduLat() + addy);

            int extranexti = (extraindex + 1) % (2 * originCount);
            x = midpoints[i].getBaiduLng() + offsetx;
            y = midpoints[i].getBaiduLat() + offsety;
            extrapoints[extranexti] = new ReferencePoint(x, y);
            //朝 originPoint[i]方向收缩
            addx = (extrapoints[extranexti].getBaiduLng() - originPoints.get(i).getBaiduLng()) * scale;
            addy = (extrapoints[extranexti].getBaiduLat() - originPoints.get(i).getBaiduLat()) * scale;
            extrapoints[extranexti].setBaiduLng(originPoints.get(i).getBaiduLng() + addx);
            extrapoints[extranexti].setBaiduLat(originPoints.get(i).getBaiduLat() + addy);
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> tempP = null;
        ReferencePoint controlPoint[] = new ReferencePoint[4];
        //生成4控制点，产生贝塞尔曲线
        for (int i = 0; i < originCount; i++) {
            controlPoint[0] = originPoints.get(i);
            int extraindex = 2 * i;
            controlPoint[1] = extrapoints[extraindex + 1];
            int extranexti = (extraindex + 2) % (2 * originCount);
            controlPoint[2] = extrapoints[extranexti];
            int nexti = (i + 1) % originCount;
            controlPoint[3] = originPoints.get(nexti);
            float u = 1;
            while (u >= 0) {
                //计算贝塞尔曲线
                double px = bezier3funcX(u, controlPoint);
                double py = bezier3funcY(u, controlPoint);
                //u的步长决定曲线的疏密
                u -= step;
                tempP = new HashMap<String, Object>();
                tempP.put("lng", px);
                tempP.put("lat", py);
                //存入曲线点
                result.add(tempP);
            }
        }

        return result;
    }

    private double bezier3funcX(float uu, ReferencePoint[] controlPoint) {
        double part0 = controlPoint[0].getBaiduLng() * uu * uu * uu;
        double part1 = 3 * controlPoint[1].getBaiduLng() * uu * uu * (1 - uu);
        double part2 = 3 * controlPoint[2].getBaiduLng() * uu * (1 - uu) * (1 - uu);
        double part3 = controlPoint[3].getBaiduLng() * (1 - uu) * (1 - uu) * (1 - uu);
        return part0 + part1 + part2 + part3;
    }

    private double bezier3funcY(float uu, ReferencePoint[] controlPoint) {
        double part0 = controlPoint[0].getBaiduLat() * uu * uu * uu;
        double part1 = 3 * controlPoint[1].getBaiduLat() * uu * uu * (1 - uu);
        double part2 = 3 * controlPoint[2].getBaiduLat() * uu * (1 - uu) * (1 - uu);
        double part3 = controlPoint[3].getBaiduLat() * (1 - uu) * (1 - uu) * (1 - uu);
        return part0 + part1 + part2 + part3;
    }

    /**
     * 以服务小区为中心，从第一象限到第四象限，按照角度大小排序并归纳叠加成16个点
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    private List<ReferencePoint> ascAndInduce16Points(List<ReferencePoint> points,
                                                      double cellbaiduLng, double cellbaiduLat) {

        List<ReferencePoint> one = new ArrayList<ReferencePoint>();
        List<ReferencePoint> two = new ArrayList<ReferencePoint>();
        List<ReferencePoint> three = new ArrayList<ReferencePoint>();
        List<ReferencePoint> four = new ArrayList<ReferencePoint>();

        //划分落在每个象限的点

        for (ReferencePoint p : points) {
            //第一象限
            if (p.getBaiduLng() >= cellbaiduLng && p.getBaiduLat() > cellbaiduLat) {
                one.add(p);
            }
            //第二象限
            if (p.getBaiduLng() < cellbaiduLng && p.getBaiduLat() >= cellbaiduLat) {
                two.add(p);
            }
            //第三象限
            if (p.getBaiduLng() <= cellbaiduLng && p.getBaiduLat() < cellbaiduLat) {
                three.add(p);
            }
            //第四象限
            if (p.getBaiduLng() > cellbaiduLng && p.getBaiduLat() <= cellbaiduLat) {
                four.add(p);
            }
        }

        ReferencePoint temp = null;
        double xxi, yyi, xxj, yyj, sini, sinj;
        //将各自象限内的点按照角度划分顺序
        //第一象限
        for (int i = 0; i < one.size(); i++) {
            for (int j = i + 1; j < one.size(); j++) {
                xxi = one.get(i).getBaiduLng() - cellbaiduLng;
                yyi = one.get(i).getBaiduLat() - cellbaiduLat;
                xxj = one.get(j).getBaiduLng() - cellbaiduLng;
                yyj = one.get(j).getBaiduLat() - cellbaiduLat;
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
                xxi = two.get(i).getBaiduLng() - cellbaiduLng;
                yyi = two.get(i).getBaiduLat() - cellbaiduLat;
                xxj = two.get(j).getBaiduLng() - cellbaiduLng;
                yyj = two.get(j).getBaiduLat() - cellbaiduLat;
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
                xxi = three.get(i).getBaiduLng() - cellbaiduLng;
                yyi = three.get(i).getBaiduLat() - cellbaiduLat;
                xxj = three.get(j).getBaiduLng() - cellbaiduLng;
                yyj = three.get(j).getBaiduLat() - cellbaiduLat;
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
                xxi = four.get(i).getBaiduLng() - cellbaiduLng;
                yyi = four.get(i).getBaiduLat() - cellbaiduLat;
                xxj = four.get(j).getBaiduLng() - cellbaiduLng;
                yyj = four.get(j).getBaiduLat() - cellbaiduLat;
                sini = yyi / (Math.sqrt(xxi * xxi + yyi * yyi));
                sinj = yyj / (Math.sqrt(xxj * xxj + yyj * yyj));
                if (sini > sinj) {
                    temp = four.get(i);
                    four.set(i, four.get(j));
                    four.set(j, temp);
                }
            }
        }

        List<ReferencePoint> res = new ArrayList<ReferencePoint>();
        //int oneNum=0,twoNum=0,threeNum=0,fourNum=0;
        //对每一象限分成两份，取每份的数据矢量相加作为一个参考点，即每个象限取两个参考点
        ReferencePoint tempPoint = null;
        double tempLng = cellbaiduLng;
        double tempLat = cellbaiduLat;
        int num = 0;
        if (one.size() < 4) {
            for (int i = 0; i < one.size(); i++) {
                res.add(one.get(i));
            }
        } else {
            num = one.size() / 4;
            for (int i = 0; i < num; i++) {
                tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 1; i < num * 2; i++) {
                tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 2; i < num * 3; i++) {
                tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 3; i < num * 4; i++) {
                tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 4; i < one.size(); i++) {
                tempLng += (one.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (one.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
        }

        //第二象限
        if (two.size() < 4) {
            for (int i = 0; i < two.size(); i++) {
                res.add(two.get(i));
            }
        } else {
            num = two.size() / 4;
            for (int i = 0; i < num; i++) {
                tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 1; i < num * 2; i++) {
                tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 2; i < num * 3; i++) {
                tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 3; i < num * 4; i++) {
                tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 4; i < two.size(); i++) {
                tempLng += (two.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (two.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
        }
        //第三象限
        if (three.size() < 4) {
            for (int i = 0; i < three.size(); i++) {
                res.add(three.get(i));
            }
        } else {
            num = three.size() / 4;
            for (int i = 0; i < num; i++) {
                tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 1; i < num * 2; i++) {
                tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 2; i < num * 3; i++) {
                tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 3; i < num * 4; i++) {
                tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 4; i < three.size(); i++) {
                tempLng += (three.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (three.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
        }
        //第四象限
        if (four.size() < 4) {
            for (int i = 0; i < four.size(); i++) {
                res.add(four.get(i));
            }
        } else {
            num = four.size() / 4;
            for (int i = 0; i < num; i++) {
                tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
            }
            
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 1; i < num * 2; i++) {
                tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 2; i < num * 3; i++) {
                tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 3; i < num * 4; i++) {
                tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
            tempLng = cellbaiduLng;
            tempLat = cellbaiduLat;
            for (int i = num * 4; i < four.size(); i++) {
                tempLng += (four.get(i).getBaiduLng() - cellbaiduLng);
                tempLat += (four.get(i).getBaiduLat() - cellbaiduLat);
            }
            tempPoint = new ReferencePoint(tempLng, tempLat);
            res.add(tempPoint);
        }

        return res;
    }
}
