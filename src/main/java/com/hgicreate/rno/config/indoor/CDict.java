package com.hgicreate.rno.config.indoor;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 系统字典
 */
@Component
public class CDict {

    public final HashMap<String, String> FLOOR_TYPE = new HashMap<String, String>() {
        {
            put("LOOBY", "大堂");
            put("FUEST", "客房");
            put("EFOOD", "餐饮");
        }
    };

    public final HashMap<String, String> EQUT_STATUS = new HashMap<String, String>() {
        {
            put("A", "正常");
            put("E", "编辑中");
            put("X", "禁用");
        }
    };
    public final HashMap<String, String> EQUT_TYPE = new HashMap<String, String>() {
        {
            put("PBX", "交换机");
            put("AP", "AP");
        }
    };
    public final HashMap<String, String> EQUT_BRANDS = new HashMap<String, String>() {
        {
            put("BRAN1", "品牌1");
            put("BRAN2", "品牌2");
            put("BRAN3", "品牌3");
        }
    };
    public final HashMap<String, String> EQUT_FACTORY = new HashMap<String, String>() {
        {
            put("ZXING", "中兴");
            put("HUA_S", "华三");
            put("HUA_W", "华为");
        }
    };
    public final HashMap<String, String> POI_TYPE = new HashMap<String, String>() {
        {
            put("ANTS", "室内天线");
            put("DING", "餐饮");
            put("SHOP", "购物");
            put("FUNC", "休闲娱乐");
        }
    };
    public final HashMap<String, String> ANT_FREQUENCYS = new HashMap<String, String>() {
        {
            put("945", "GSM900(930～960)");
            put("1842", "DCS1800(1805～1880)");
            put("1900", "TD-SCDMA BF(1880～1920)");
            put("2020", "TD-SCDMA A(2015～2025)");
            put("2350", "TD-SCDMA CE(2300～2400)");
            put("1897", "TD-LTE F(1880～1915)");
            put("2017", "TD-LTE A(2010～2025)");
            put("2345", "TD-LTE E(2320～2370)");
            put("2442", "WLAN(2400～2484)");
        }
    };
    public final HashMap<String, String> POI_STATUS = new HashMap<String, String>() {
        {
            put("A", "正常");
            put("E", "编辑中");
            put("X", "禁用");
        }
    };
    public final HashMap<String, String> SVG_LAYER_TYPE = new HashMap<String, String>() {
        {
            put("OUT_WALL", "OUT_W");
            put("AP", "AP");
            put("POI", "POI");
        }
    };
    public final HashMap<String, String> BUILD_TYPE = new HashMap<String, String>() {
        {
            put("MALL_", "大型商场");
            put("OFFIC", "写字楼");
            put("LARGE", "大型场馆");
            put("TRAFF", "交通枢纽");
        }
    };
    public final HashMap<String, String> BUILD_STATUS = new HashMap<String, String>() {
        {
            put("A", "正常");
            put("E", "编辑中");
            put("X", "失效");
        }
    };
    public final HashMap<String, String> PLANEGRAPH_UNIT = new HashMap<String, String>() {
        {
            put("px", "像素");
            put("cm", "厘米");
            put("mm", "毫米");
            put("in", "英寸");
            put("pt", "点");
            put("pc", "Picas");
            put("em", "Ems");
            put("ex", "Exs");
        }
    };
    public final HashMap<String, String> DEFAULT_PLANEGRAPH = new HashMap<String, String>() {
        {
            // 1像素对应0.15m
            put("DW_SCALE", "0.15");
            put("DW_UNIT", "px");
            put("BACKGROUD_COLOR", "#FFFFFF");
            put("STATUS", "E");
        }
    };
}
