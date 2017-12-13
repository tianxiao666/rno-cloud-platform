package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.gsm.AreaRectangle;
import com.hgicreate.rno.mapper.gsm.GsmDynamicCoverageMapper;
import com.hgicreate.rno.web.rest.gsm.vm.Area;
import com.hgicreate.rno.web.rest.gsm.vm.Cell;
import com.hgicreate.rno.web.rest.gsm.vm.DynamicCoverageData;
import com.hgicreate.rno.web.rest.gsm.vm.DynamicCoverageResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RnoLteGisServiceImpl implements RnoLteGisService {

    private static double defaultImgSize = 0.005; // 曲线点距离服务小区的长度系数0.005
    private static double evalCoeff = 0.3; // 服务小区评估方向的长度系数，经调试0.3较好
    private static float scale = 0.6f; //控制点收缩系数 ，经调试0.6较好
    private static double step = 0.01; //曲线密度(0-1)0.01
    private static double defaultImgSizeCoff = 10; // 矢量长度系数10
    @Autowired
    private GsmDynamicCoverageMapper gsmDynamicCoverageMapper;
    @Autowired
    private CommonRestClient commonRestClient;
    @Autowired
    private RnoCommonDao rnoCommonDao;


    @Override
    public List<Area> getSpecialSubAreasByAccountAndParentArea(String account, long parentAreaId, String subAreaLevel) {
        return null;
    }

    /**
     * 通过区域ID获取指定区域的地图经纬度纠偏map集合
     *
     * @param areaid
     * @param mapType 地图类型
     * @return
     * @author chao.xj
     * @date 2013-10-28上午10:49:29
     */
    @Override
    public Map<AreaRectangle, List<Map<String, Object>>> getSpecialAreaRnoMapLnglatRelaGpsMapList(long areaid,
                                                                                                  String mapType) {
        List<Map<String, Object>> list = rnoCommonDao.getSpecialAreaRnoMapLnglatRelaGpsList(areaid, mapType);
        log.debug("list ==={}", list);
        Map<AreaRectangle, List<Map<String, Object>>> map = new HashMap<AreaRectangle, List<Map<String, Object>>>();

        AreaRectangle rec;
        String topleft = null;
        String bottomright = null;
        double minLng, maxLng, minLat, maxLat;
        for (Map<String, Object> rnoMapLnglatRelaGps : list) {
            topleft = (String) rnoMapLnglatRelaGps.get("TOPLEFT");
            bottomright = (String) rnoMapLnglatRelaGps.get("BOTTOMRIGHT");

            if (topleft == null || bottomright == null) {
                log.error("topleft =" + topleft + ",bottomRight=" + bottomright);
                continue;
            }

            String[] tls = topleft.split(",");
            String[] brs = bottomright.split(",");
            if (tls.length != 2 || tls.length != 2) {
                log.error("topleft =" + topleft + ",bottomRight=" + bottomright + ",用逗号分解得到的数组元素数量不等于2");
                continue;
            }

            try {
                minLng = Double.parseDouble(tls[0]);
                maxLng = Double.parseDouble(brs[0]);
                minLat = Double.parseDouble(brs[1]);
                maxLat = Double.parseDouble(tls[1]);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            rec = new AreaRectangle(minLng, maxLng, minLat, maxLat);

            if (!map.containsKey(rec)) {
                List<Map<String, Object>> rmgpsList = new ArrayList<Map<String, Object>>();
                rmgpsList.add(rnoMapLnglatRelaGps);
                map.put(rec, rmgpsList);
            } else {
                map.get(rec).add(rnoMapLnglatRelaGps);
            }
        }
        return map;
    }

    @Override
    public DynamicCoverageResult get4GDynaCoverageData2ByCityAndDate(String lteCellId, long cityId, String dates, double imgCoeff, double imgSizeCoeff) {
        return null;
    }

    @Override
    public List<Map<String, String>> getLteDynamicCoverageShapeData(String lteCellId, long cityId, String dates, String inOrOut) {
        return null;
    }


    /**
     * 小区名与小区对象的映射
     */
    private Map<String, Cell> getCellMap(long cityId) {
        Map<String, Cell> cellMap = new HashMap<>();
        log.debug("getCellMap, cityId={}", cityId);
        List<Cell> lteCells = commonRestClient.findByAreaId(cityId);
        if (lteCells != null) {
            for (Cell lteCell : lteCells) {
                cellMap.put(lteCell.getCellId(), lteCell);
            }
        }
        return cellMap;
    }

    /**
     * 查询动态覆盖数据
     * out 出干扰：即被别人所检测  in 入干扰：即检测到别人
     */
    private List<DynamicCoverageData> queryDynaCoverDataFromSparkMrTable(long cityId, String lteCellId, String dates, String inOrOut) {
        String dateArr[] = dates.split(",");
        StringBuilder sb = new StringBuilder("(");
        for (String date : dateArr) {
            date = date.replaceAll("-", "");
            sb.append("'").append(date).append("',");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        lteCellId = "'" + lteCellId + "'";
        if ("IN".equalsIgnoreCase(inOrOut)) {
            return gsmDynamicCoverageMapper.queryDynamicCoverageInData(cityId, lteCellId, sb.toString());
        } else {
            return gsmDynamicCoverageMapper.queryDynamicCoverageOutData(cityId, lteCellId, sb.toString());
        }
    }

    /**
     * 计算4G动态覆盖坐标点数据
     */
    private DynamicCoverageResult calc4GDynaCoveragePointsData2(List<DynamicCoverageData> dynaCoverData, double imgCoeff, double imgSizeCoeff, Map<String, Cell> cellMap) {
        long startTime = System.currentTimeMillis();
        DynamicCoverageResult result = new DynamicCoverageResult();
        //主小区经纬度信息
        double cellLng = dynaCoverData.get(0).getCellLon();
        double cellLat = dynaCoverData.get(0).getCellLat();
        //如果图形大小系数未设置，取默认值
        if (imgSizeCoeff == 0) {
            imgSizeCoeff = defaultImgSize;
        }
        List<ReferencePointCellId> points = new ArrayList<>();
        ReferencePointCellId point;
        //通过经纬度存储对应的矢量长度
        Map<String, Double> lnglatToVector = new HashMap<>();
        /*                          A小区方向评估【CalAngle-A】 开始                 */
        /* 小区评估方向矢量X，Y坐标 */
        double cellEvalX = 0;
        double cellEvalY = 0;
        for (DynamicCoverageData one : dynaCoverData) {
            double val = one.getVal1();
            //MR小区数据即为邻区数据，这里的经纬度信息即为邻区经纬度【不同】
            double lng = one.getNcellLon();
            double lat = one.getNcellLat();
            String ncellId = one.getNcellId();
            //增加这里获取的小区经纬度信息即为主小区经纬度信息【相同】
            if (lng == 0 || lat == 0) {
                continue;
            }
            //邻区的经纬度信息【不同】
            double lngDiff = Math.abs(lng - cellLng);
            double latDiff = Math.abs(lat - cellLat);
            double cosV = lngDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff)); //正弦值->余弦值
            double sinV = latDiff / (Math.sqrt(lngDiff * lngDiff + latDiff * latDiff)); //余弦值->正弦值
            cellEvalX += val * cosV;
            cellEvalY += val * sinV;
            //缓存没有任何变化的小区经纬度信息为后续覆盖图使用
            point = new ReferencePointCellId(lng, lat, ncellId);
            points.add(point);
            //缓存矢量长度：此时矢量长度是关联度*站间距，后面还要引入图形大小系数
            //存储邻区到服务小区的角度大小
            lnglatToVector.put(ncellId, val);
        }
        /* 小区评估方向矢量经纬坐标 */
        double cellEvalLng = cellLng + cellEvalX;
        double cellEvalLat = cellLat + cellEvalY;
        List<Map<String, Object>> vectorPoints = new ArrayList<>();
        Map<String, Object> vectorPoint = new HashMap<>();
        vectorPoint.put("lng", cellEvalLng);
        vectorPoint.put("lat", cellEvalLat);
        vectorPoints.add(vectorPoint);
        result.setVectorPoint(vectorPoints);
        /*                               A小区方向评估【CalAngle-A】 结束                                     */
        long etime = System.currentTimeMillis();
        log.debug("数据通过基准点转为百度坐标，耗时：" + (etime - startTime));
        //以服务小区为中心，从第一象限到第四象限，按照角度大小排序
        //归纳16个点
        List<ReferencePoint> originPoints = ascAndInduce16PointsStage2(points, cellLng, cellLat, lnglatToVector, cellMap, imgCoeff);
        /* 直接将16个象限坐标点返回  绘制曲线，由前端贝塞尔类库实现*/
        List<Map<String, Object>> oriPointsList = new ArrayList<>();
        Map<String, Object> ori = new HashMap<>();
        for (int i = 0; i < originPoints.size(); i++) {
            ori.put(i + "", originPoints.get(i));
        }
        oriPointsList.add(ori);
        result.setCurvePoints(oriPointsList);
        etime = System.currentTimeMillis();
        log.debug("通过数据计算折线点集合，耗时：" + (etime - startTime));
        return result;
    }

    /*
     * 以服务小区为中心，从第一象限到第四象限，按照角度大小排序并归纳叠加成16个点 阶段2
     */
    private List<ReferencePoint> ascAndInduce16PointsStage2(List<ReferencePointCellId> points, double cellbaiduLng, double cellbaiduLat, Map<String, Double> lnglatToVector, Map<String, Cell> cellMap, double imgCoeff) {

        List<ReferencePointCellId> one = new ArrayList<>();
        List<ReferencePointCellId> two = new ArrayList<>();
        List<ReferencePointCellId> three = new ArrayList<>();
        List<ReferencePointCellId> four = new ArrayList<>();
        List<ReferencePoint> sumPoints = new ArrayList<>();
        int oneCnt = 0, twoCnt = 0, threeCnt = 0, fourCnt = 0;
        //0.0006相对好些
        //划分落在每个象限的点
        //逆时针划分象限
        for (ReferencePointCellId p : points) {
            //第一象限
            if (p.getBaiduLng() >= cellbaiduLng && p.getBaiduLat() > cellbaiduLat) {
                one.add(p);
                oneCnt++;
            }
            //第二象限
            if (p.getBaiduLng() < cellbaiduLng && p.getBaiduLat() >= cellbaiduLat) {
                two.add(p);
                twoCnt++;
            }
            //第三象限
            if (p.getBaiduLng() <= cellbaiduLng && p.getBaiduLat() < cellbaiduLat) {
                three.add(p);
                threeCnt++;
            }
            //第四象限
            if (p.getBaiduLng() > cellbaiduLng && p.getBaiduLat() <= cellbaiduLat) {
                four.add(p);
                fourCnt++;
            }
        }
        ReferencePoint temp;
        double xxi, yyi, xxj, yyj, angleV, ideaDis, corDegree, cosV, sinV, angleV1;
        double ncellLng, ncellLat;
        String ncellId;
        double vectorLenth1 = 0;
        double vectorLenth2 = 0;
        double vectorLenth3 = 0;
        double vectorLenth4 = 0;
        double xxi1 = 0, xxi2 = 0, xxi3 = 0, xxi4 = 0, yyi1 = 0, yyi2 = 0, yyi3 = 0, yyi4 = 0;
        //将各自象限内的点按照角度划分顺序
        //第一象限
        for (ReferencePointCellId anOne : one) {
            ncellLng = anOne.getBaiduLng();
            ncellLat = anOne.getBaiduLat();
            ncellId = anOne.getCellId();
            xxi = ncellLng - cellbaiduLng;
            yyi = ncellLat - cellbaiduLat;
            angleV = calcAngle(xxi, yyi);
            //矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
            //还需要重构小区站间距，经度_纬度的方式
            ideaDis = cellMap.get(ncellId).getStationSpace();
            corDegree = Double.parseDouble(lnglatToVector.get(ncellId).toString());
            if (angleV >= 0 && angleV < 22.5) {
                //决定矢量长度
                vectorLenth1 += ideaDis * defaultImgSize * corDegree;
                //经纬度矢量相加决定方向角
                xxi1 += xxi;
                yyi1 += yyi;
            } else if (angleV >= 22.5 && angleV < 45) {
                vectorLenth2 += ideaDis * defaultImgSize * corDegree;
                xxi2 += xxi;
                yyi2 += yyi;
            } else if (angleV >= 45 && angleV < 67.5) {
                vectorLenth3 += ideaDis * defaultImgSize * corDegree;
                xxi3 += xxi;
                yyi3 += yyi;
            } else if (angleV >= 67.5 && angleV < 90) {
                vectorLenth4 += ideaDis * defaultImgSize * corDegree;
                xxi4 += xxi;
                yyi4 += yyi;
            }
        }
        /* 第一象限第一区域坐标矢量 */
        if (vectorLenth1 != 0 && (xxi1 != 0 || yyi1 != 0)) {
            cosV = xxi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            sinV = yyi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            xxi1 = vectorLenth1 * cosV;
            yyi1 = vectorLenth1 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1, cellbaiduLat + yyi1));
        }
        /* 第一象限第二区域坐标矢量 */
        if (vectorLenth2 != 0 && (xxi2 != 0 || yyi2 != 0)) {
            cosV = xxi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            sinV = yyi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            xxi2 = vectorLenth2 * cosV;
            yyi2 = vectorLenth2 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2, cellbaiduLat + yyi2));
        }
        /* 第一象限第三区域坐标矢量 */
        if (vectorLenth3 != 0 && (xxi3 != 0 || yyi3 != 0)) {
            cosV = xxi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            sinV = yyi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            xxi3 = vectorLenth3 * cosV;
            yyi3 = vectorLenth3 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3, cellbaiduLat + yyi3));
        }
        /* 第一象限第四区域坐标矢量 */
        if (vectorLenth4 != 0 && (xxi4 != 0 || yyi4 != 0)) {
            cosV = xxi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            sinV = yyi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            xxi4 = vectorLenth4 * cosV;
            yyi4 = vectorLenth4 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4, cellbaiduLat + yyi4));
        }
        /* 初始化 */
        vectorLenth1 = 0;
        vectorLenth2 = 0;
        vectorLenth3 = 0;
        vectorLenth4 = 0;
        xxi1 = 0;
        yyi1 = 0;
        xxi2 = 0;
        yyi2 = 0;
        xxi3 = 0;
        yyi3 = 0;
        xxi4 = 0;
        yyi4 = 0;
        //第二象限
        for (ReferencePointCellId aTwo : two) {
            ncellLng = aTwo.getBaiduLng();
            ncellLat = aTwo.getBaiduLat();
            ncellId = aTwo.getCellId();
            xxi = ncellLng - cellbaiduLng;
            yyi = ncellLat - cellbaiduLat;
            angleV = calcAngle(xxi, yyi);
            //矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
            //还需要重构小区站间距，经度_纬度的方式
            ideaDis = cellMap.get(ncellId).getStationSpace();
            corDegree = Double.parseDouble(lnglatToVector.get(ncellId).toString());
            if (angleV >= 90 && angleV < 112.5) {
                //决定矢量长度
                vectorLenth1 += ideaDis * defaultImgSize * corDegree;
                //经纬度矢量相加决定方向角
                xxi1 += xxi;
                yyi1 += yyi;
            } else if (angleV >= 112.5 && angleV < 135) {
                vectorLenth2 += ideaDis * defaultImgSize * corDegree;
                xxi2 += xxi;
                yyi2 += yyi;
            } else if (angleV >= 135 && angleV < 157.5) {
                vectorLenth3 += ideaDis * defaultImgSize * corDegree;
                xxi3 += xxi;
                yyi3 += yyi;
            } else if (angleV >= 157.5 && angleV < 180) {
                vectorLenth4 += ideaDis * defaultImgSize * corDegree;
                xxi4 += xxi;
                yyi4 += yyi;
            }
        }
        /* 第二象限第一区域坐标矢量 */
        if (vectorLenth1 != 0 && (xxi1 != 0 || yyi1 != 0)) {
            cosV = xxi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            sinV = yyi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            xxi1 = vectorLenth1 * cosV;
            yyi1 = vectorLenth1 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1, cellbaiduLat + yyi1));
        }
        /* 第二象限第二区域坐标矢量 */
        if (vectorLenth2 != 0 && (xxi2 != 0 || yyi2 != 0)) {
            cosV = xxi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            sinV = yyi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            xxi2 = vectorLenth2 * cosV;
            yyi2 = vectorLenth2 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2, cellbaiduLat + yyi2));
        }
        /* 第二象限第三区域坐标矢量 */
        if (vectorLenth3 != 0 && (xxi3 != 0 || yyi3 != 0)) {
            cosV = xxi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            sinV = yyi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            xxi3 = vectorLenth3 * cosV;
            yyi3 = vectorLenth3 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3, cellbaiduLat + yyi3));
        }
        /* 第二象限第四区域坐标矢量 */
        if (vectorLenth4 != 0 && (xxi4 != 0 || yyi4 != 0)) {
            cosV = xxi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            sinV = yyi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            xxi4 = vectorLenth4 * cosV;
            yyi4 = vectorLenth4 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4, cellbaiduLat + yyi4));
        }
        /* 初始化 */
        vectorLenth1 = 0;
        vectorLenth2 = 0;
        vectorLenth3 = 0;
        vectorLenth4 = 0;
        xxi1 = 0;
        yyi1 = 0;
        xxi2 = 0;
        yyi2 = 0;
        xxi3 = 0;
        yyi3 = 0;
        xxi4 = 0;
        yyi4 = 0;
        //第三象限
        for (ReferencePointCellId aThree : three) {
            ncellLng = aThree.getBaiduLng();
            ncellLat = aThree.getBaiduLat();
            ncellId = aThree.getCellId();
            xxi = ncellLng - cellbaiduLng;
            yyi = ncellLat - cellbaiduLat;
            angleV = calcAngle(xxi, yyi);
            //矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
            //还需要重构小区站间距，经度_纬度的方式
            ideaDis = cellMap.get(ncellId).getStationSpace();
            corDegree = Double.parseDouble(lnglatToVector.get(ncellId).toString());
            if (angleV >= 180 && angleV < 202.5) {
                //决定矢量长度
                vectorLenth1 += ideaDis * defaultImgSize * corDegree;
                //经纬度矢量相加决定方向角
                xxi1 += xxi;
                yyi1 += yyi;
            } else if (angleV >= 202.5 && angleV < 225) {
                vectorLenth2 += ideaDis * defaultImgSize * corDegree;
                xxi2 += xxi;
                yyi2 += yyi;
            } else if (angleV >= 225 && angleV < 247.5) {
                vectorLenth3 += ideaDis * defaultImgSize * corDegree;
                xxi3 += xxi;
                yyi3 += yyi;
            } else if (angleV >= 247.5 && angleV < 270) {
                vectorLenth4 += ideaDis * defaultImgSize * corDegree;
                xxi4 += xxi;
                yyi4 += yyi;
            }
        }
        /* 第三象限第一区域坐标矢量 */
        if (vectorLenth1 != 0 && (xxi1 != 0 || yyi1 != 0)) {
            cosV = xxi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            sinV = yyi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            xxi1 = vectorLenth1 * cosV;
            yyi1 = vectorLenth1 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1, cellbaiduLat + yyi1));
        }
        /* 第三象限第二区域坐标矢量 */
        if (vectorLenth2 != 0 && (xxi2 != 0 || yyi2 != 0)) {
            cosV = xxi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            sinV = yyi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            xxi2 = vectorLenth2 * cosV;
            yyi2 = vectorLenth2 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2, cellbaiduLat + yyi2));
        }
        /* 第三象限第三区域坐标矢量 */
        if (vectorLenth3 != 0 && (xxi3 != 0 || yyi3 != 0)) {
            cosV = xxi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            sinV = yyi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            xxi3 = vectorLenth3 * cosV;
            yyi3 = vectorLenth3 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3, cellbaiduLat + yyi3));
        }
        /* 第三象限第四区域坐标矢量 */
        if (vectorLenth4 != 0 && (xxi4 != 0 || yyi4 != 0)) {
            cosV = xxi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            sinV = yyi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            xxi4 = vectorLenth4 * cosV;
            yyi4 = vectorLenth4 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4, cellbaiduLat + yyi4));
        }
        /* 初始化 */
        vectorLenth1 = 0;
        vectorLenth2 = 0;
        vectorLenth3 = 0;
        vectorLenth4 = 0;
        xxi1 = 0;
        yyi1 = 0;
        xxi2 = 0;
        yyi2 = 0;
        xxi3 = 0;
        yyi3 = 0;
        xxi4 = 0;
        yyi4 = 0;
        //第四象限
        for (ReferencePointCellId aFour : four) {

            ncellLng = aFour.getBaiduLng();
            ncellLat = aFour.getBaiduLat();
            ncellId = aFour.getCellId();
            xxi = ncellLng - cellbaiduLng;
            yyi = ncellLat - cellbaiduLat;
            angleV = calcAngle(xxi, yyi);
            //矢量长度=关联度* 图形大小系数（固定，暂无默认值，需后期调整）*理想覆盖
            //还需要重构小区站间距，经度_纬度的方式
            ideaDis = cellMap.get(ncellId).getStationSpace();
            corDegree = Double.parseDouble(lnglatToVector.get(ncellId).toString());
            if (angleV >= 270 && angleV < 292.5) {
                //决定矢量长度
                vectorLenth1 += ideaDis * defaultImgSize * corDegree;
                //经纬度矢量相加决定方向角
                xxi1 += xxi;
                yyi1 += yyi;
            } else if (angleV >= 292.5 && angleV < 315) {
                vectorLenth2 += ideaDis * defaultImgSize * corDegree;
                xxi2 += xxi;
                yyi2 += yyi;
            } else if (angleV >= 315 && angleV < 337.5) {
                vectorLenth3 += ideaDis * defaultImgSize * corDegree;
                xxi3 += xxi;
                yyi3 += yyi;
            } else if (angleV >= 337.5 && angleV < 360) {
                vectorLenth4 += ideaDis * defaultImgSize * corDegree;
                xxi4 += xxi;
                yyi4 += yyi;
            }
        }
        /* 第四象限第一区域坐标矢量 */
        if (vectorLenth1 != 0 && (xxi1 != 0 || yyi1 != 0)) {
            cosV = xxi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            sinV = yyi1 / Math.sqrt(xxi1 * xxi1 + yyi1 * yyi1);
            xxi1 = vectorLenth1 * cosV;
            yyi1 = vectorLenth1 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi1, cellbaiduLat + yyi1));
        }
        /* 第四象限第二区域坐标矢量 */
        if (vectorLenth2 != 0 && (xxi2 != 0 || yyi2 != 0)) {
            cosV = xxi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            sinV = yyi2 / Math.sqrt(xxi2 * xxi2 + yyi2 * yyi2);
            xxi2 = vectorLenth2 * cosV;
            yyi2 = vectorLenth2 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi2, cellbaiduLat + yyi2));
        }
        /* 第四象限第三区域坐标矢量 */
        if (vectorLenth3 != 0 && (xxi3 != 0 || yyi3 != 0)) {
            cosV = xxi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            sinV = yyi3 / Math.sqrt(xxi3 * xxi3 + yyi3 * yyi3);
            xxi3 = vectorLenth3 * cosV;
            yyi3 = vectorLenth3 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi3, cellbaiduLat + yyi3));
        }
        /* 第四象限第四区域坐标矢量 */
        if (vectorLenth4 != 0 && (xxi4 != 0 || yyi4 != 0)) {
            cosV = xxi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            sinV = yyi4 / Math.sqrt(xxi4 * xxi4 + yyi4 * yyi4);
            xxi4 = vectorLenth4 * cosV;
            yyi4 = vectorLenth4 * sinV;
            sumPoints.add(new ReferencePoint(cellbaiduLng + xxi4, cellbaiduLat + yyi4));
        }
        /*  十六分区域两两相邻矢量再次相加 绿色矢量（回落折线图）=相邻两个矢量相加 * 折线图形状系数（默认为0.3）  */
        double shapeFactor = 0.3;
        List<ReferencePoint> tempPoints = new ArrayList<>();
        for (int i = 0; i < sumPoints.size(); i++) {
            for (int j = i + 1; j < sumPoints.size(); j++) {
                xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
                yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
                xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
                yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
                //夹角大于180度放弃两坐标点矢量累加
                if (Math.abs(calcAngle(xxi, yyi) - calcAngle(xxj, yyj)) > 180) {
                    continue;
                }
                //矢量X
                xxi = xxi + xxj;
                //矢量Y
                yyi = yyi + yyj;
                vectorLenth1 = Math.sqrt(xxi * xxi + yyi * yyi) * shapeFactor;
                cosV = xxi / Math.sqrt(xxi * xxi + yyi * yyi);
                sinV = yyi / Math.sqrt(xxi * xxi + yyi * yyi);
                xxi = vectorLenth1 * cosV;
                yyi = vectorLenth1 * sinV;
                tempPoints.add(new ReferencePoint(cellbaiduLng + xxi, cellbaiduLat + yyi));
                if (j == sumPoints.size() - 1) {
                    xxi = sumPoints.get(0).getBaiduLng() - cellbaiduLng;
                    yyi = sumPoints.get(0).getBaiduLat() - cellbaiduLat;
                    //矢量X
                    xxi = xxi + xxj;
                    //矢量Y
                    yyi = yyi + yyj;
                    vectorLenth1 = Math.sqrt(xxi * xxi + yyi * yyi) * shapeFactor;
                    cosV = xxi / Math.sqrt(xxi * xxi + yyi * yyi);
                    sinV = yyi / Math.sqrt(xxi * xxi + yyi * yyi);
                    xxi = vectorLenth1 * cosV;
                    yyi = vectorLenth1 * sinV;
                    tempPoints.add(new ReferencePoint(cellbaiduLng + xxi, cellbaiduLat + yyi));
                } else {
                    break;
                }
            }
        }
        if (tempPoints.size() != 0) {
            //合并
            sumPoints.addAll(tempPoints);
        }
        // 再次角度大小排序 依次从小到大排列
        //保存矢量长度
        double verLens[] = new double[sumPoints.size()];
        for (int i = 0; i < sumPoints.size(); i++) {

            xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
            yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
            vectorLenth1 = Math.sqrt(xxi * xxi + yyi * yyi);
//			verLens.put(i+"",vectorLenth1);
            verLens[i] = vectorLenth1;
            for (int j = i + 1; j < sumPoints.size(); j++) {
                xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
                yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
                xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
                yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
                angleV = calcAngle(xxi, yyi);
                angleV1 = calcAngle(xxj, yyj);
                if (angleV > angleV1) {
                    temp = sumPoints.get(i);
                    sumPoints.set(i, sumPoints.get(j));
                    sumPoints.set(j, temp);
                }
            }
        }
        // 再次矢量长度大小排序 依次从小到大排列
        for (int i = 0; i < sumPoints.size(); i++) {
            for (int j = i + 1; j < sumPoints.size(); j++) {
                xxi = sumPoints.get(i).getBaiduLng() - cellbaiduLng;
                yyi = sumPoints.get(i).getBaiduLat() - cellbaiduLat;
                xxj = sumPoints.get(j).getBaiduLng() - cellbaiduLng;
                yyj = sumPoints.get(j).getBaiduLat() - cellbaiduLat;
                angleV = calcAngle(xxi, yyi);
                angleV1 = calcAngle(xxj, yyj);
                if (angleV > angleV1) {
                    temp = sumPoints.get(i);
                    sumPoints.set(i, sumPoints.get(j));
                    sumPoints.set(j, temp);
                }
            }
        }
        //根据集合中最大最小值的比值确定各矢量坐标到原点坐标收缩的倍数
        Arrays.sort(verLens);
        double maxVal = verLens[sumPoints.size() - 1];
        double minVal = verLens[0];
        log.debug("最大矢量长度===" + maxVal);
        log.debug("最小矢量长度===" + minVal);
        double ratio = maxVal / minVal;
        log.debug("比值大小   ratio================" + ratio);
        for (ReferencePoint sumPoint : sumPoints) {
            //策略调整
            if (maxVal > 0.04) {
                if (ratio > 10000) {
                    ratio = 100;
                } else if (10000 >= ratio && ratio > 100) {
                    ratio = 100;
                } else if (ratio < 100) {
                    ratio = 5;
                }
            } else if (0.04 >= maxVal && maxVal > 0.00099) {
                ratio = 1;
            } else if (maxVal <= 0.00099) {
                ratio = 0.1;
            }
            xxi = sumPoint.getBaiduLng() - cellbaiduLng;
            yyi = sumPoint.getBaiduLat() - cellbaiduLat;
            cosV = xxi / Math.sqrt(xxi * xxi + yyi * yyi);
            sinV = yyi / Math.sqrt(xxi * xxi + yyi * yyi);
            xxi = cosV * Math.sqrt(xxi * xxi + yyi * yyi) / ratio * imgCoeff;
            yyi = sinV * Math.sqrt(xxi * xxi + yyi * yyi) / ratio * imgCoeff;
            sumPoint.setBaiduLng(cellbaiduLng + xxi);
            sumPoint.setBaiduLat(cellbaiduLat + yyi);
        }
        log.debug("方法结束   sumPoints================" + sumPoints.size());
        return sumPoints;
    }

    private double calcAngle(double x, double y) {
        double d;
        double tan = y / x;
        if (x == 0.0 && y == 0.0) {
            d = 0.0;
        } else if (x >= 0.0 && y == 0.0) {
            d = 90.0;
        } else if (x < 0 && y == 0) {
            d = 180.0;
        } else if (x == 0.0 && y < 0.0) {
            d = 270.0;
        } else {
            d = Math.atan(tan);
        }
        if (x > 0.0 && y > 0.0) {
            d = d * 180 / Math.PI;
        } else if (x < 0.0 && y != 0.0) {
            d = 180 + d * 180 / Math.PI;
        } else if (x > 0.0 && y < 0.0) {
            d = 360 + d * 180 / Math.PI;
        }
        return d;
    }

    /**
     * 转换lte小区与某同站小区的pci
     */
    @Override
    public Map<String, Boolean> changeLteCellPci(String cell1, String pci1, String cell2, String pci2) {
        boolean res1 = commonRestClient.changeCellPci(cell1, Integer.parseInt(pci1));
        boolean res2 = commonRestClient.changeCellPci(cell2, Integer.parseInt(pci2));
        Map<String, Boolean> map = new HashMap<>();
        if (res1 && res2) {
            map.put("flag", true);
        } else {
            map.put("flag", false);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> getInCellInfo(int jobId, String cellId) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("cellId", cellId);
        return gsmDynamicCoverageMapper.getInCellInfo(map);
    }
    /* PCI评估 */

    @Override
    public List<Map<String, Object>> getOutCellInfo(int jobId, String ncellId) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("ncellId", ncellId);
        return gsmDynamicCoverageMapper.getOutCellInfo(map);
    }

    @Override
    public List<Map<String, Object>> getPciCellInfo(int jobId, String cellId) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("cellId", cellId);
        return gsmDynamicCoverageMapper.getPciCellInfo(map);
    }

    @Data
    class ReferencePoint {
        double baiduLng;
        double baiduLat;

        ReferencePoint(double baiduLng, double baiduLat) {
            this.baiduLng = baiduLng;
            this.baiduLat = baiduLat;
        }
    }

    @Data
    class ReferencePointCellId {
        double baiduLng;
        double baiduLat;
        String cellId;

        ReferencePointCellId(double baiduLng, double baiduLat, String cellId) {
            this.baiduLng = baiduLng;
            this.baiduLat = baiduLat;
            this.cellId = cellId;
        }
    }
}
