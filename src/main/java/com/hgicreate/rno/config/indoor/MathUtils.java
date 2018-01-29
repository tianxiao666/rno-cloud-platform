package com.hgicreate.rno.config.indoor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Transactional
public class MathUtils {

    /**
     * 计算两个点之间的距离
     */
    public static double getDistanceForTwoPoint(double x1, double y1, double x2, double y2) {
        return (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    /**
     * 计算两个圆的交点
     *
     * 设w=(r1*r1-r2*r2-a1*a1+a2*a2-b1*b1+b2*b2+2*b1-2*b2)/(2*a2-2*a1)
     * 则计算两个圆交点的方程转换为x = wy 带入方程得到(w*w+1)*y*y -
     * (2*a1*e+2*b1)*y+(a1*a1+b1*b1-r1*r1)=0 然后根据一元二次方程的求解
     *
     * @param r1 圆1的半径
     * @param a1 圆1坐标x,不能等于a2
     * @param b1 圆1坐标y
     * @param r2 圆2的半径
     * @param a2 圆2坐标x
     * @param b2 圆2坐标y
     * @return 返回两个圆交点的坐标
     */
    public List<Map<String, Double>> getIntersectionForTwoCircle(double r1, double a1, double b1, double r2, double a2, double b2) {
        if (a1 != a2) {
            // (x-a1)*(x-a1)+(y-b1)*(y-b1)=r1*r1
            // (x-a2)*(x-a2)+(y-b2)*(y-b2)=r2*r2
            // 下式减上式得
            // x = v * y + w
            double w = (r1 * r1 - r2 * r2 - a1 * a1 + a2 * a2 - b1 * b1 + b2 * b2) / (2 * (a2 - a1));
            double v = (b1 - b2) / (a2 - a1);
            double a = (v * v + 1);
            double b = (2 * w * v - 2 * a1 * v - 2 * b1);
            double c = (w * w - r1 * r1 + a1 * a1 - 2 * a1 * w + b1 * b1);
            // a*y*y+b*y+c=0
            Map<String, Double> p = getUQESolution(a, b, c);
            if (p != null) {
                List<Map<String, Double>> intersections = new ArrayList<Map<String, Double>>();
                Map<String, Double> map = new HashMap<String, Double>();
                // 第一个交点
                map.put("y", p.get("x"));
                map.put("x", w + v * map.get("y"));
                intersections.add(map);
                if (p.get("x") != p.get("y")) {
                    map = new HashMap<String, Double>();
                    // 第二个交点
                    map.put("y", p.get("y"));
                    map.put("x", w + v * map.get("y"));
                    intersections.add(map);
                }
                return (intersections);
            }
        }
        return (null);
    }

    /**
     * Unitary quadratic equation 获取一元二次方程的解
     * @param a 不能为0
     */
    private Map<String, Double> getUQESolution(double a, double b, double c) {
        if (a != 0) {
            Map<String, Double> p = new HashMap<String, Double>();
            double deta = getDeta(a, b, c);
            if (deta >= 0) {
                p.put("x", (-b + Math.sqrt(deta)) / (2 * a));
                p.put("y", (deta == 0) ? p.get("x") : ((-b - Math.sqrt(deta)) / (2 * a)));
                return (p);
            }
        }
        return (null);
    }

    /**
     * 一元二次方程deta
     */
    private double getDeta(double a, double b, double c) {
        return (b * b - 4 * a * c);
    }

    /**
     * 计算两个圆的交点组成的直线，与另外一个圆的交点
     *
     * @param r   第三个圆的半径
     * @param r_x 第三个圆坐标x
     * @param r_y 第三个圆坐标y
     * @param x0  直线的两个点x,不能等于x1
     * @param y0  直线的两个点y
     * @param x1  直线的两个点x
     * @param y1  直线的两个点y
     */
    public List<Map<String, Double>> getIntersectionForCircleAndLine(double r, double r_x, double r_y, double x0, double y0, double x1, double y1) {
        if (x0 != x1) {
            // y=kx+b
            double k = (y0 - y1) / (x0 - x1);
            double b = y0 - k * x0;
            return (getIntersectionForCircleAndLineKB(r, r_x, r_y, k, b));
        }
        return (null);
    }

    private List<Map<String, Double>> getIntersectionForCircleAndLineKB(double r, double r_x, double r_y, double k, double b) {
        double a1 = 1 + k * k;
        double b1 = -2 * (r_x + k * r_y - k * b);
        double c1 = r_x * r_x + r_y * r_y - 2 * b * r_y - r * r + b * b;
        Map<String, Double> p = getUQESolution(a1, b1, c1);
        if (p != null) {
            List<Map<String, Double>> intersections = new ArrayList<Map<String, Double>>();
            Map<String, Double> map = new HashMap<String, Double>();
            // 第一个交点
            map.put("x", p.get("x"));
            map.put("y", k * map.get("x") + b);
            intersections.add(map);
            if (p.get("x") != p.get("y")) {
                map = new HashMap<String, Double>();
                // 第二个交点
                map.put("x", p.get("y"));
                map.put("y", k * map.get("x") + b);
                intersections.add(map);
            }
            return (intersections);
        }
        return (null);
    }
}
