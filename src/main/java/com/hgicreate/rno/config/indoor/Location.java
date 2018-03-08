package com.hgicreate.rno.config.indoor;

import com.hgicreate.rno.mapper.indoor.api.ApiDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chao.xj
 */
@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class Location {

    private final ApiDataMapper apiDataMapper;
    private final CDict cDict;
    private final MathUtils mathUtil;

    public Location(ApiDataMapper apiDataMapper, CDict cDict, MathUtils mathUtil) {
        this.apiDataMapper = apiDataMapper;
        this.cDict = cDict;
        this.mathUtil = mathUtil;
    }

    public Map<String, Double> getLocation(JSONArray apList, long buildingId, long floorId, long drawMapId) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject ap1 = null;
        JSONObject ap2 = null;
        JSONObject ap3 = null;
        Map<String, Double> location = null;
        try {
            JSONObject ap = null;
            StringBuilder sb = new StringBuilder();
            String macBssids = "";
            String tmp = "";
            for (int i = 0; i < apList.length(); i++) {
                ap = apList.getJSONObject(i);
                tmp = ap.get("MAC_BSSID").toString().toUpperCase();
                sb.append("'").append(tmp).append("'").append(",");
            }
            macBssids = sb.substring(0, sb.length() - 1);
            map.put("macBssids", macBssids);
            map.put("buildingId", buildingId);
            map.put("floorId", floorId);
            map.put("drawMapId", drawMapId);
            List<Map<String, Object>> apMapLists = apiDataMapper.getApListByMacbssids(map);
            Map<String, Map<String, Object>> macBssidMaps = new HashMap<>();
            if (null != apMapLists && !apMapLists.isEmpty()) {
                apMapLists.forEach(map1 -> macBssidMaps.put(map1.get("MAC_BSSID").toString(), map1));
            }
            Map<String, Object> apinfo = null;
            Map<String, Object> ap1info = null;
            Map<String, Object> ap2info = null;
            Map<String, Object> ap3info = null;
            if (!macBssidMaps.isEmpty()) {
                for (int i = 0; i < apList.length(); i++) {
                    ap = apList.getJSONObject(i);
                    apinfo = macBssidMaps.get(ap.getString("MAC_BSSID").toUpperCase());
                    if (null != apinfo && !apinfo.isEmpty() && !isCloseToAp(apinfo, ap1info, ap2info, ap3info)) {
                        if (ap1 == null) {
                            ap1 = ap;
                            ap1info = apinfo;
                        } else {
                            if (ap.getDouble("LEVEL") > ap1.getDouble("LEVEL")) {
                                ap3 = ap2;
                                ap2 = ap1;
                                ap1 = ap;
                                ap3info = ap2info;
                                ap2info = ap1info;
                                ap1info = apinfo;
                            } else {
                                if (ap2 == null) {
                                    ap2 = ap;
                                    ap2info = apinfo;
                                } else {
                                    if (ap.getDouble("LEVEL") > ap2.getDouble("LEVEL")) {
                                        ap3 = ap2;
                                        ap2 = ap;
                                        ap3info = ap2info;
                                        ap2info = apinfo;
                                    } else {
                                        if (ap3 == null) {
                                            ap3 = ap;
                                            ap3info = apinfo;
                                        } else {
                                            if (ap.getDouble("LEVEL") > ap3.getDouble("LEVEL")) {
                                                ap3 = ap;
                                                ap3info = apinfo;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                location = calcLocationByAp(macBssidMaps, ap1, ap2, ap3);
            }
        } catch (Exception e) {
            log.error("getLocation catch error:" + e.getMessage());
        }
        return location;
    }

    /**
     * 算一下是否和ap1,ap2,ap3太近，在1米之内
     */
    private boolean isCloseToAp(Map<String, Object> apinfo, Map<String, Object> ap1info, Map<String, Object> ap2info, Map<String, Object> ap3info) {
        double closeMeter = 1;
        if (ap1info != null) {
            if (getDistanceForTwoAp(apinfo, ap1info) < closeMeter) {
                return true;
            }
        }
        if (ap2info != null) {
            if (getDistanceForTwoAp(apinfo, ap2info) < closeMeter) {
                return true;
            }
        }
        if (ap3info != null) {
            if (getDistanceForTwoAp(apinfo, ap3info) < closeMeter) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取两ap间的距离（米）
     */
    private double getDistanceForTwoAp(Map<String, Object> ap1info, Map<String, Object> ap2info) {
        return toMeter(MathUtils.getDistanceForTwoPoint(Double.parseDouble(ap1info.get("POSITION_X").toString()),
                Double.parseDouble(ap1info.get("POSITION_Y").toString()),
                Double.parseDouble(ap2info.get("POSITION_X").toString()),
                Double.parseDouble(ap2info.get("POSITION_Y").toString())));
    }

    /**
     * 像素to米
     */
    public double toMeter(double px) {
        double scale = Double.parseDouble(cDict.DEFAULT_PLANEGRAPH.get("DW_SCALE"));
        return (px * scale);
    }

    /**
     * 米到像素
     */
    public double toPx(double m) {
        double scale = Double.parseDouble(cDict.DEFAULT_PLANEGRAPH.get("DW_SCALE"));
        return (m / scale);
    }

    private Map<String, Double> calcLocationByAp(Map<String, Map<String, Object>> apinfolist, JSONObject ap1, JSONObject ap2, JSONObject ap3) {
        Map<String, Object> ap1info = null;
        Map<String, Object> ap2info = null;
        Map<String, Object> ap3info = null;
        if (null != ap1) {
            ap1info = apinfolist.get(ap1.getString("MAC_BSSID").toUpperCase());
            double ap1X = toMeter(Double.parseDouble(ap1info.get("POSITION_X").toString()));
            double ap1Y = toMeter(Double.parseDouble(ap1info.get("POSITION_Y").toString()));
            double ap1R = getRadiusForSingal(ap1.getDouble("LEVEL"), null, null, null, null, null, null);
            if (ap2 != null) {
                ap2info = apinfolist.get(ap2.getString("MAC_BSSID").toUpperCase());
                double ap2X = toMeter(Double.parseDouble(ap2info.get("POSITION_X").toString()));
                double ap2Y = toMeter(Double.parseDouble(ap2info.get("POSITION_Y").toString()));
                double ap2R = getRadiusForSingal(ap2.getDouble("LEVEL"), null, null, null, null, null, null);
                List<Map<String, Double>> in12 = mathUtil.getIntersectionForTwoCircle(ap1R, ap1X, ap1Y, ap2R, ap2X, ap2Y);
                if (ap3 != null) {
                    ap3info = apinfolist.get(ap3.getString("MAC_BSSID").toUpperCase());
                    double ap3X = toMeter(Double.parseDouble(ap3info.get("POSITION_X").toString()));
                    double ap3Y = toMeter(Double.parseDouble(ap3info.get("POSITION_Y").toString()));
                    double ap3R = getRadiusForSingal(ap3.getDouble("LEVEL"), null, null, null, null, null, null);
                    List<Map<String, Double>> in13 = mathUtil.getIntersectionForTwoCircle(ap1R, ap1X, ap1Y, ap3R, ap3X, ap3Y);
                    List<Map<String, Double>> in23 = mathUtil.getIntersectionForTwoCircle(ap2R, ap2X, ap2Y, ap3R, ap3X, ap3Y);
                    Map<String, Double> location123 = computeLocation(ap3R, ap3X, ap3Y, in12);
                    Map<String, Double> location132 = computeLocation(ap2R, ap2X, ap2Y, in13);
                    Map<String, Double> location231 = computeLocation(ap1R, ap1X, ap1Y, in23);
                    if (location123 != null) {
                        location123.put("x", toPx(location123.get("x")));
                        location123.put("y", toPx(location123.get("y")));
                        return (location123);
                    } else {
                        if (location132 != null) {
                            location132.put("x", toPx(location132.get("x")));
                            location132.put("y", toPx(location132.get("y")));
                            return (location132);
                        } else {
                            if (location231 != null) {
                                location231.put("x", toPx(location231.get("x")));
                                location231.put("y", toPx(location231.get("y")));
                                return (location231);
                            }
                        }
                    }
                } else {
                    if (in12 != null) {
                        return new HashMap<String, Double>() {{
                            put("x", toPx(in12.get(0).get("x")));
                            put("y", toPx(in12.get(0).get("y")));
                        }};
                    }
                }
            }
            return new HashMap<String, Double>() {{
                put("x", toPx(ap1X));
                put("y", toPx(ap1Y));
            }};
        }
        return (null);
    }

    /**
     * @param pd  测量点的信号值
     * @param nW  测量点与BS间的墙壁数量
     * @param c   最大墙壁数，超过C个墙壁则会对信号造成影响
     * @param waF 墙壁衰减因子
     * @param n   信号值因距离衰减的系数
     * @param pdo 测量基准点的信号值
     * @param dod 测量基准点距BS的距离
     * @return double 信号圆的半径（米）
     */
    public double getRadiusForSingal(Double pd, Double nW, Double c, Double waF, Double n, Double pdo, Double dod) {
        double e = 2.7182818284590452354;
        double w = 0;
        double d = 0;
        if (null == nW) {
            nW = 0D;
        }
        if (null == c) {
            c = 1D;
        }
        if (null == waF) {
            waF = 10D;
        }
        if (null == n) {
            n = 2D;
        }
        if (null == pdo) {
            pdo = -35D;
        }
        if (null == dod) {
            dod = 4D;
        }

        if (nW < c) {
            w = nW * waF;
        } else {
            w = c * waF;
        }
        d = dod * Math.pow(e, (pdo - pd - w) / (n * 10));
        return (d);
    }

    private Map<String, Double> computeLocation(double r, double rX, double rY, List<Map<String, Double>> intersections) {
        if (intersections != null) {
            double x0 = intersections.get(0).get("x");
            double y0 = intersections.get(0).get("y");
            double x1 = x0;
            double y1 = y0;
            if (intersections.get(1) != null) {
                x1 = intersections.get(1).get("x");
                y1 = intersections.get(1).get("y");
            }
            return (computeLineCircle(r, rX, rY, x0, y0, x1, y1));
        }
        return (null);
    }

    /**
     * 计算两个圆的交点组成的直线，与另外一个圆的交点
     *
     * @param r   第三个圆的半径
     * @param rX 第三个圆坐标x
     * @param rY 第三个圆坐标y
     * @param x0  直线的两个点x
     * @param y0  直线的两个点y
     * @param x1  直线的两个点x
     * @param y1  直线的两个点y
     */
    private Map<String, Double> computeLineCircle(double r, double rX, double rY, double x0, double y0, double x1, double y1) {
        // 两个圆有交点,开始计算交点的直线公式
        // 第三个圆与第一个交点的距离
        double distance0 = MathUtils.getDistanceForTwoPoint(rX, rY, x0, y0);
        // 第三个圆与第二个交点的距离
        double distance1 = MathUtils.getDistanceForTwoPoint(rX, rY, x1, y1);
        Map<String, Double> p = new HashMap<String, Double>();
        // 两个圆的直线与第三个圆没有交点，且距离大于第三个圆的半径
        if (r < distance0 && r <= distance1) {
            if (distance0 < distance1) {
                p.put("x", x0);
                p.put("y", y0);
            } else {
                p.put("x", x1);
                p.put("y", y1);
            }
        } else if (r >= distance0 && r >= distance1) {
            // 两个圆的直线与第三个圆没有交点，且距离小于第三个圆的半径
            if (distance0 < distance1) {
                p.put("x", x1);
                p.put("y", y1);
            } else {
                p.put("x", x0);
                p.put("y", y0);
            }
        } else {
            List<Map<String, Double>> intersections = mathUtil.getIntersectionForCircleAndLine(r, rX, rY, x0, y0, x1, y1);
            if (intersections != null) {
                p = intersections.get(0);
                if (intersections.get(1) != null) {
                    double min = (x0 > x1) ? x1 : x0;
                    double max = (x0 > x1) ? x0 : x1;
                    String xais = "x";
                    if ((min <= intersections.get(1).get(xais) && (intersections.get(1).get(xais) <= max))) {
                        p = intersections.get(1);
                    }
                }
            }
        }
        if (null == p) {
            p = null;
        }
        return (p);
    }

    public static void main(String[] args) {

    }
}
