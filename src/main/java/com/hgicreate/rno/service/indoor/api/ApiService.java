package com.hgicreate.rno.service.indoor.api;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.config.indoor.CDict;
import com.hgicreate.rno.config.indoor.Location;
import com.hgicreate.rno.config.indoor.SvgUtils;
import com.hgicreate.rno.mapper.indoor.api.ApiDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.floor;

/**
 * 移动终端接口服务类
 */
@Slf4j
@Service
@Transactional
public class ApiService extends Api {

    private final ApiDataMapper apiDataMapper;
    private final SvgUtils svgUtil;
    private final CDict cDict;
    private final Location location;

    public ApiService(ApiDataMapper apiDataMapper, SvgUtils svgUtil, CDict cDict, Location location) {
        this.apiDataMapper = apiDataMapper;
        this.svgUtil = svgUtil;
        this.cDict = cDict;
        this.location = location;
    }

    /**
     * 获取楼层列表
     */
    public String getBuildingFloorList(String jsonArrayObj) {

        String message = "";
        String res = "";
        if (null == jsonArrayObj || "".equals(jsonArrayObj)) {
            message = "参数为空";
            return renderErrorJson(message);
        }
        List<Map<String, Object>> buildingFloorList = null;
        try {
            // 转换成为JSONObject对象
            JSONObject jsons = new JSONObject(jsonArrayObj);
            // 判断是否有场所ID传入
            if (jsons.has("BUILDING_ID")) {
                long buildingId = jsons.getLong("BUILDING_ID");
                buildingFloorList = apiDataMapper.getBuildingFloorList(buildingId);
            } else {
                message = "信息错误！";
            }
        } catch (Exception e) {
            message = "解析参数出错：" + "catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            String content = "{\"BuildingFloorList\":" + objectToJson(objectToJson(buildingFloorList)) + "}";
            return renderSuccessJson(content,false);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取室内地图
     */
    public String getBuildingFloorMap(String jsonArrayObj) {
        String message = "";
        String res = "";
        String content = "";
        if (null == jsonArrayObj || "".equals(jsonArrayObj)) {
            message = "参数为空";
            return renderErrorJson(message);
        }

        try {
            // 转换成为JSONObject对象
            JSONObject jsons = new JSONObject(jsonArrayObj);
            Long buildingId = null;
            if (jsons.has("BUILDING_ID")) {
                buildingId = jsons.getLong("BUILDING_ID");
            }
            Long floorId = null;
            if (jsons.has("FLOOR_ID") && !jsons.getString("FLOOR_ID").isEmpty()) {
                floorId = jsons.getLong("FLOOR_ID");
            }
            Map<String, Object> response = svgUtil.getSvg(buildingId, floorId, null);
            if (null == response.get("error")) {
                content = "{" +
                        "\"FLOOR_ID\":" + response.get("FLOOR_ID") + "," +
                        "\"DRAW_MAP_ID\":" + response.get("DRAW_MAP_ID") + "," +
                        "\"LayerList\":" + objectToJson(objectToJson(response.get("LayerList"))) + "," +
                        "\"SVGSRC\":" + objectToJson(response.get("SVGSRC")) +
                        "}";

            } else {
                message = response.get("error").toString();
            }
        } catch (Exception e) {
            message = "解析参数出错：" + "catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            return renderSuccessJson(content,false);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取场所类型
     */
    public String getBuildingTypeList() {
        String content = "";
        Map<String, String> buildingType = cDict.BUILD_TYPE;
        if (null != buildingType || !buildingType.isEmpty()) {
            List<Map<String, String>> buildingTypeList = new ArrayList<Map<String, String>>();
            buildingType.forEach((buildType, buildTypeName) -> {
                Map<String, String> obj = new HashMap<String, String>();
                obj.put("BUILD_TYPE", buildType);
                obj.put("BUILD_TYPE_NAME", buildTypeName);
                buildingTypeList.add(obj);
            });
            content = "{\"BuildingTypeList\":" + objectToJson(objectToJson(buildingTypeList)) + "}";
        } else {
            content = "{}";
        }
        return renderSuccessJson(content,false);
    }

    /**
     * 获取楼层类型
     */
    public String getFloorTypeList() {
        String content = "";
        Map<String, String> floorTypes = cDict.FLOOR_TYPE;
        if (null != floorTypes || !floorTypes.isEmpty()) {
            List<Map<String, String>> floorTypeList = new ArrayList<Map<String, String>>();
            floorTypes.forEach((floorType, floorTypeName) -> {
                Map<String, String> obj = new HashMap<String, String>();
                obj.put("FLOOR_TYPE", floorType);
                obj.put("FLOOR_TYPE_NAME", floorTypeName);
                floorTypeList.add(obj);
            });
            content = "{\"FloorTypeList\":" + objectToJson(objectToJson(floorTypeList)) + "}";
        } else {
            content = "{}";
        }
        return renderSuccessJson(content,false);
    }

    /**
     * 获取移动终端信号坐标栅格周期集合数据接口
     */
    public String getMtSignalList(String jsonArrayObj) {
        String message = "";
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> lists = null;
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            if (jsonObj.has("BUILDING_ID")) {
                map.put("buildingId", jsonObj.getString("BUILDING_ID"));
            } else {
                message = "BUILDING_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("FLOOR_ID")) {
                map.put("floorId", jsonObj.getString("FLOOR_ID"));
            } else {
                message = "FLOOR_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("DRAW_MAP_ID")) {
                map.put("drawMapId", jsonObj.getString("DRAW_MAP_ID"));
            } else {
                message = "DRAW_MAP_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("SIGNAL_TYPE")) {
                map.put("signalType", jsonObj.getString("SIGNAL_TYPE"));
            } else {
                message = "SIGNAL_TYPE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("START_DATE")) {
                map.put("startDate", jsonObj.getString("START_DATE"));
            } else {
                message = "START_DATE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("END_DATE")) {
                map.put("endDate", jsonObj.getString("END_DATE"));
            } else {
                message = "END_DATE属性值为空！";
                return renderErrorJson(message);
            }
            // 先确定点坐标归属方格区域
            double x = Double.parseDouble(jsonObj.getString("PLANE_X"));
            double y = Double.parseDouble(jsonObj.getString("PLANE_Y"));
            double length = Double.parseDouble(jsonObj.getString("WIDTH").replaceAll("[a-zA-Z]","" ));
            double width = Double.parseDouble(jsonObj.getString("HEIGHT").replaceAll("[a-zA-Z]","" ));
            // 长分10格
            int m = 10;
            // 宽分10格
            int n = 10;
            // 单元方格长
            double grid_len = length / m;
            // 单元方格宽
            double grid_wid = width / n;
            // 确定矩阵栅格列
            double col = floor(x / grid_len);
            // 确定矩阵栅格行
            double row = floor(y / grid_wid);
            // 左上
            double grid_ltx = grid_len * col;
            double grid_lty = grid_wid * row;
            // 右下
            double grid_rbx = grid_len * (col + 1);
            double grid_rby = grid_wid * (row + 1);
            // 区域确定结束
            map.put("gridLtx", grid_ltx);
            map.put("gridLty", grid_lty);
            map.put("gridRbx", grid_rbx);
            map.put("gridRby", grid_rby);
            lists = apiDataMapper.getMtSignalGridMeaData(map);
        } catch (Exception e) {
            message = "移动终端图层栅格化信号测量数据输出数据库出错......！catch error:" + e.getMessage();
            log.error("getMtSignalGridMeaData 移动终端图层栅格化信号测量数据输出数据库出错......！，exception={}", e);
            return renderErrorJson(message);
        }
        log.debug("getMtSignalGridMeaData 数据属性={},数据大小={}", lists.toString(),lists.size());
        map.clear();
        if ("".equals(message)){
            return renderSuccessJson(objectToJson(lists),false);
        }
        return renderErrorJson(message);
    }

    /**
     * 移动终端信号强度测量数据采集接口
     */
    public String mtSignalCollection(String jsonArrayObj) {
        String message = "";
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            if (jsonObj.has("BUILDING_ID")) {
                map.put("buildingId", jsonObj.getString("BUILDING_ID"));
            } else {
                message = "BUILDING_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("SIGNAL")) {
                map.put("signal", jsonObj.getString("SIGNAL"));
            } else {
                message = "SIGNAL属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("FLOOR_ID")) {
                map.put("floorId", jsonObj.getString("FLOOR_ID"));
            } else {
                message = "FLOOR_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("DRAW_MAP_ID")) {
                map.put("drawMapId", jsonObj.getString("DRAW_MAP_ID"));
            } else {
                message = "DRAW_MAP_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("LONGITUDE")) {
                map.put("longitude", jsonObj.getString("LONGITUDE"));
            } else {
                message = "LONGITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("LATITUDE")) {
                map.put("latitude", jsonObj.getString("LATITUDE"));
            } else {
                message = "LATITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("PLANE_X")) {
                map.put("planeX", jsonObj.getString("PLANE_X"));
            } else {
                message = "PLANE_X属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("PLANE_Y")) {
                map.put("planeY", jsonObj.getString("PLANE_Y"));
            } else {
                message = "PLANE_Y属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("SIGNAL_TYPE")) {
                map.put("signalType", jsonObj.getString("SIGNAL_TYPE"));
            } else {
                message = "SIGNAL_TYPE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("DEVICE_ID")) {
                map.put("deviceId", jsonObj.getString("DEVICE_ID"));
            } else {
                message = "DEVICE_ID属性值为空！";
                return renderErrorJson(message);
            }
            // new日期对象
            Date date = new Date();
            // 转换提日期输出格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String meaDate = dateFormat.format(date);
            map.put("meaDate", meaDate);
            apiDataMapper.saveMtSignalData(map);
        } catch (Exception e) {
            message = "移动终端信号测量数据录入数据库出错......！ catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            message = "采集成功";
            return renderSuccessJson(message,true);
        }
        return renderErrorJson(message);
    }

    /**
     * 理想AP测量数据采集接口
     */
    public String idealApCollection(String jsonArrayObj) {
        String message = "";
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            if (jsonObj.has("BUILDING_ID")) {
                map.put("buildingId", jsonObj.getString("BUILDING_ID"));
            } else {
                message = "BUILDING_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("AP_LEVELS")) {
                map.put("apLevels", jsonObj.getString("AP_LEVELS"));
            } else {
                message = "AP_LEVELS属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("FLOOR_ID")) {
                map.put("floorId", jsonObj.getString("FLOOR_ID"));
            } else {
                message = "FLOOR_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("DRAW_MAP_ID")) {
                map.put("drawMapId", jsonObj.getString("DRAW_MAP_ID"));
            } else {
                message = "DRAW_MAP_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("LONGITUDE")) {
                map.put("longitude", jsonObj.getString("LONGITUDE"));
            } else {
                message = "LONGITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("LATITUDE")) {
                map.put("latitude", jsonObj.getString("LATITUDE"));
            } else {
                message = "LATITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("PLANE_X")) {
                map.put("planeX", jsonObj.getString("PLANE_X"));
            } else {
                message = "PLANE_X属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("PLANE_Y")) {
                map.put("planeY", jsonObj.getString("PLANE_Y"));
            } else {
                message = "PLANE_Y属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("Derection")) {
                map.put("phoneDirection", jsonObj.getString("Derection"));
            } else {
                message = "Derection属性值为空！";
                return renderErrorJson(message);
            }
            // new日期对象
            Date date = new Date();
            // 转换提日期输出格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String meaDate = dateFormat.format(date);
            map.put("meaDate", meaDate);
            apiDataMapper.saveIdealApData(map);
        } catch (Exception e) {
            message = "理想AP测量数据录入数据库出错......！ catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            message = "采集成功";
            return renderSuccessJson(message,true);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取poi信息列表
     */
    public String getPoiList(String jsonArrayObj) {
        String message = "";
        boolean flag = false;
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> results = null;
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            if (jsonObj.has("BUILDING_ID")) {
                flag = true;
                map.put("buildingId", jsonObj.getString("BUILDING_ID"));
            } else {
                message = "BUILDING_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("FLOOR_ID")) {
                flag = true;
                map.put("floorId", jsonObj.getString("FLOOR_ID"));
            } else {
                message = "FLOOR_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("DRAW_MAP_ID")) {
                flag = true;
                map.put("drawMapId", jsonObj.getString("DRAW_MAP_ID"));
            } else {
                message = "DRAW_MAP_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("POI_TYPE")) {
                flag = true;
                map.put("poiType", jsonObj.getString("POI_TYPE"));
            } else {
                message = "POI_TYPE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("POI_ID")) {
                flag = true;
                map.put("poiId", jsonObj.getString("POI_ID"));
            } else {
                message = "POI_ID属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("SVG_ID")) {
                flag = true;
                map.put("svgId", jsonObj.getString("SVG_ID"));
            } else {
                message = "SVG_ID属性值为空！";
                return renderErrorJson(message);
            }
            // 过滤状态无效的
            map.put("status", "X");
            if (flag) {
                results = apiDataMapper.getPoiList(map);
                if (results != null || !results.isEmpty()) {
                    // 更名
                    results.forEach((s) -> {
                        if (s.containsKey("ANT_FREQUENCY")) {
                            s.put("FREQUENCY", s.get("ANT_FREQUENCY"));
                        }
                        if (s.containsKey("ANT_POWER")) {
                            s.put("POWER", s.get("ANT_POWER"));
                        }
                    });
                } else {
                    message = "没有查找到符合的poi！";
                }
            } else {
                message = "查找poi条件错误！";
            }
            message = "采集成功";
        } catch (Exception e) {
            message = "理想AP测量数据录入数据库出错......！ catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            return renderSuccessJson(objectToJson(results),true);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取附近场所接口
     */
    public String getNearbyBuildingList(String jsonArrayObj) {
        String message = "";
        boolean flag = false;
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String content = "";
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            Double longitude = 0D;
            Double latitude = 0D;
            Double range;
            if (jsonObj.has("BUILDING_TYPE")) {
                map.put("buildingType", jsonObj.getString("BUILDING_TYPE"));
            }
            if (jsonObj.has("LONGITUDE")) {
                longitude = jsonObj.getDouble("LONGITUDE");
                // 坐标值处理，让它与数据库相匹配
                longitude = SvgUtils.getEncodeLatLng(longitude);
                map.put("longitude", longitude);
            } else {
                message = "LONGITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("LATITUDE")) {
                latitude = jsonObj.getDouble("LATITUDE");
                // 坐标值处理，让它与数据库相匹配
                latitude = SvgUtils.getEncodeLatLng(latitude);
                map.put("latitude", latitude);
            } else {
                message = "LATITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("RANGE")) {
                // 有范围值时的查询条件
                range = jsonObj.getDouble("RANGE");
                range = SvgUtils.getEncodeLatLng(range / 111.2);
                map.put("range", range);
            }
            // 过滤状态无效的
            map.put("status", "X");
            // 附近场所的数据
            List<Map<String, Object>> buildingList = apiDataMapper.getNearbyBuildingList(map);
            // 把数据库的坐标值转成真实坐标值
            longitude = SvgUtils.getDecodeLatLng(longitude);
            latitude = SvgUtils.getDecodeLatLng(latitude);
            if (null != buildingList && !buildingList.isEmpty()) {
                String picWhere = "";
                // 场所图片信息的查询条件
                for (Map<String, Object> buildingMap : buildingList) {
                    if ("".equals(picWhere)) {
                        picWhere = picWhere + " (BUILDING_ID=" + buildingMap.get("BUILDING_ID");
                    } else {
                        picWhere = picWhere + " or BUILDING_ID=" + buildingMap.get("BUILDING_ID");
                    }
                }
                picWhere = picWhere + ") and FLOOR_ID is null";
                // 查询场所图片信息
                List<Map<String, Object>> buildingIconList = apiDataMapper.getPic(picWhere);
                String filePath = "";
                // 定义媒体库物理路径
                String medialibPath = System.getProperty("user.dir") + File.separator + "medialib/";
                for (Map<String, Object> buildingMap : buildingList) {
                    // 返回数据的生成
                    buildingMap.put("LT_LONGITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("LT_LONGITUDEL").toString())));
                    buildingMap.put("LT_LATITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("LT_LATITUDEL").toString())));
                    buildingMap.put("RB_LONGITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("RB_LONGITUDEL").toString())));
                    buildingMap.put("RB_LATITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("RB_LATITUDEL").toString())));
                    double buildingLONGITUDEL = (Double.parseDouble(buildingMap.get("LT_LONGITUDEL").toString())
                            + Double.parseDouble(buildingMap.get("RB_LONGITUDEL").toString())) / 2;
                    double buildingLATITUDEL = (Double.parseDouble(buildingMap.get("LT_LATITUDEL").toString())
                            + Double.parseDouble(buildingMap.get("RB_LATITUDEL").toString())) / 2;
                    buildingMap.put("DISTANCE", SvgUtils.getLatLngDistance(latitude, longitude, buildingLATITUDEL, buildingLONGITUDEL) / 1000);
                    if (null != buildingIconList && !buildingIconList.isEmpty()) {
                        // 场所与图片信息相匹配
                        for (Map<String, Object> buildingIconMap : buildingIconList) {
                            if (buildingIconMap.get("BUILDING_ID").toString().equals(buildingIconMap.get("BUILDING_ID").toString())) {
                                filePath = medialibPath + buildingIconMap.get("PATH").toString() + "/" + buildingIconMap.get("FILENAME").toString() +
                                        "." + buildingIconMap.get("PIC_TYPE").toString();
                                if (new File(filePath).exists()) {
                                    buildingMap.put("BUILDING_ICON", Constants.SF_BASE_URL + "medialib/" + buildingIconMap.get("PATH") + "/" +
                                            buildingIconMap.get("FILENAME") + "." + buildingIconMap.get("PIC_TYPE"));
                                }
                            }
                        }
                    }
                }
                // 按DISTANCE从小到大排序
                buildingList.sort((map1, map2) -> map1.get("DISTANCE").toString().
                        compareTo(map2.get("DISTANCE").toString()));
                // 生成返回数据
                content = "{\"TOTALCOUNT\":" + buildingList.size() + ",\"NearbyBuildingList\":" + objectToJson(objectToJson(buildingList)) + "}";
            } else {
                content = "{}";
            }
        } catch (Exception e) {
            message = "获取当前位置失败！ catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            return renderSuccessJson(content,false);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取新版本接口
     */
    public String checkNewVersion(String jsonArrayObj) {
        String message = "";
        String content = "";
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        // 转换成为JSONObject对象
        JSONObject jsonObj = null;
        Double versionCode = 0D;
        try {
            jsonObj = new JSONObject(jsonArrayObj);
            if (jsonObj.has("VersionCode")) {
                versionCode = jsonObj.getDouble("VersionCode");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String root = System.getProperty("user.dir") + File.separator;
        String updateApkPath = root + "apk/IndoorMap.apk";
        String updateInfPath = root + "apk/IndoorMapUpdate.ini";
        // 检测跟新文件是否存在
        if (new File(updateApkPath).exists() && new File(updateInfPath).exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(updateInfPath));
                String allLine, pair;
                Map<String, Object> newVersionInfo = new HashMap<String, Object>();
                while ((allLine = bufferedReader.readLine()) != null) {
                    pair = allLine.trim();
                    String[] pairlist = pair.split("=");
                    if (null != pairlist && pairlist.length == 2) {
                        newVersionInfo.put(pairlist[0].trim(), pairlist[1].trim());
                    }
                }
                if (Double.parseDouble(newVersionInfo.get("VersionCode").toString()) > versionCode) {
                    // 检测是否需要更新，
                    newVersionInfo.put("url", Constants.SF_BASE_URL + "apk/IndoorMap.apk");
                    // 生成返回数据
                    content = objectToJson(newVersionInfo);
                } else {
                    content = "{}";
                }
                return renderSuccessJson(content,false);
            } catch (Exception e) {
                message = "解析文件出错： catch error:" + e.getMessage();
                log.error(message);
            }
        } else {
            message = "新版本不存在！";
        }
        return renderErrorJson(message);
    }

    /**
     * 获取ap信息列表
     */
    public String getApList(String jsonArrayObj) {
        String message = "";
        String content = "";
        boolean flag = false;
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> results = null;
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            if (jsonObj.has("BUILDING_ID")) {
                flag = true;
                map.put("buildingId", jsonObj.getString("BUILDING_ID"));
            }
            if (jsonObj.has("FLOOR_ID")) {
                flag = true;
                map.put("floorId", jsonObj.getString("FLOOR_ID"));
            }
            if (jsonObj.has("DRAW_MAP_ID")) {
                flag = true;
                map.put("drawMapId", jsonObj.getString("DRAW_MAP_ID"));
            }
            if (jsonObj.has("AP_ID")) {
                flag = true;
                map.put("apId", jsonObj.getString("AP_ID"));
            }
            if (jsonObj.has("SVG_ID")) {
                flag = true;
                map.put("svgId", jsonObj.getString("SVG_ID"));
            }
            // 过滤状态无效的
            map.put("status", "X");
            if (flag) {
                results = apiDataMapper.getApList(map);
                if (null != results && !results.isEmpty()) {
                } else {
                    message = "没有查找到符合的ap！";
                }
            } else {
                message = "查找ap条件错误！";
            }
        } catch (Exception e) {
            message = "获取ap数据出错......！ catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            return renderSuccessJson(objectToJson(results),false);
        }
        return renderErrorJson(message);
    }

    /**
     * 定位接口
     */
    public String getLocation(String jsonArrayObj) {
        String message = "";
        String content = "";
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            // 转换成为JSONObject对象
            JSONObject jsons = new JSONObject(jsonArrayObj);
            JSONArray apList = null;
            Double longitude = null;
            Double latitude = null;
            Long buildingId = null;
            Long floorId = null;
            Long drawMapId = null;
            String floorName = "";
            String buildingName = "";
            String locationFloor = "";
            Map<String, Object> floorSvgInfo;
            List<Map<String, Object>> floorlist;
            if (jsons.has("LONGITUDE")) {
                longitude = jsons.getDouble("LONGITUDE");
            } else {
                message = "LONGITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsons.has("LATITUDE")) {
                latitude = jsons.getDouble("LATITUDE");
            } else {
                message = "LATITUDE属性值为空！";
                return renderErrorJson(message);
            }

            if (jsons.has("APLIST")) {
                apList = new JSONArray(jsons.getString("APLIST"));
            }

            if (jsons.has("FLOOR_ID")) {
                floorId = jsons.getLong("FLOOR_ID");
            }
            if (jsons.has("DRAW_MAP_ID")) {
                drawMapId = jsons.getLong("DRAW_MAP_ID");
            }

            if (null != apList && !"".equals(apList)) {
                if (null != longitude && null != latitude) {
                    // 坐标值处理，让它与数据库相匹配
                    longitude = SvgUtils.getEncodeLatLng(longitude);
                    latitude = SvgUtils.getEncodeLatLng(latitude);
                    map.put("longitude", longitude);
                    map.put("latitude", latitude);
                    if (jsons.has("BUILDING_ID")) {
                        buildingId = jsons.getLong("BUILDING_ID");
                        map.put("buildingId", buildingId);
                    }
                    // 定位的场所数据
                    List<Map<String, Object>> buildingList = apiDataMapper.getBuildingList(map);
                    if (null != buildingList && !buildingList.isEmpty()) {
                        // 通过场所ID获取场所名称
                        buildingName = apiDataMapper.getBuildingNameById(buildingId);
                        Map<String, Double> locationMap = location.getLocation(apList, buildingId, floorId, drawMapId);
                        if (locationMap == null) {
                            locationMap = new HashMap<String, Double>();
                        }
                        JSONObject maxAp = null;
                        JSONObject jsonObject = null;
                        // 获取AP信号最强的AP信息
                        for (int i = 0; i < apList.length(); i++) {
                            jsonObject = apList.getJSONObject(i);
                            if (maxAp == null) {
                                maxAp = jsonObject;
                            } else {
                                if (maxAp.getLong("LEVEL") < jsonObject.getLong("LEVEL")) {
                                    maxAp = jsonObject;
                                }
                            }
                        }
                        // 生成AP查询条件
                        if (maxAp.has("MAC_BSSID")) {
                            maxAp.put("MAC_BSSID", maxAp.getString("MAC_BSSID").replace("-", ":"));
                            map.put("macBssid", maxAp.getString("MAC_BSSID").toUpperCase());
                        }
                        if (maxAp.has("FREQUENCY")) {
                            map.put("frequency", maxAp.get("FREQUENCY"));
                        }
                        if (maxAp.has("CHANNEL")) {
                            map.put("channel", maxAp.get("CHANNEL"));
                        }
                            map.put("floorId", floorId);
                        // 匹配的AP信息
                        List<Map<String, Object>> apMapList = apiDataMapper.getApLocationList(map);
                        if (null != apMapList && !apMapList.isEmpty()) {
                            floorName = apiDataMapper.getFloorNameById(floorId);
                            locationFloor = buildingName + "_" + floorName;
                            // 获取平面图信息
                            floorSvgInfo = getFloorSvg(drawMapId, buildingId, floorId);
                        } else {
                            // 匹配AP不成功
                            // 传入数据有楼层ID
                            // 楼层信息
                            floorlist = apiDataMapper.getFloorListById(floorId);
                            // 传入信息没有楼层ID
                            if (null == floorlist || floorlist.isEmpty()) {
                                // 去场所数据库的第一个楼层信息
                                floorlist = apiDataMapper.getFloorListByBuildingId(buildingId);
                            }
                            // 获取平面图信息
                            floorSvgInfo = getFloorSvg(drawMapId, buildingId, Long.parseLong(floorlist.get(0).get("FLOOR_ID").toString()));
                            // 获取楼层名称
                            floorId = Long.parseLong(floorlist.get(0).get("FLOOR_ID").toString());
                            floorName = apiDataMapper.getFloorNameById(floorId);
                            locationFloor = buildingName + "_" + floorName;
                        }
                        if (null != floorSvgInfo.get("message") && !"".equals(floorSvgInfo.get("message").toString())) {//获取楼层平面图错误信息
                            message = floorSvgInfo.get("message").toString();
                        }
                        // 生成返回数据
                        content = "{" +
                                "\"X\":" + locationMap.get("x") + "," +
                                "\"Y\":" + locationMap.get("y") + "," +
                                "\"LOCATION\":" + locationFloor + "," +
                                "\"BUILDING_ID\":" + buildingId + "," +
                                "\"BUILDING_NAME\":" + buildingName + "," +
                                "\"FLOOR_ID\":" + floorId + "," +
                                "\"DRAW_MAP_ID\":" + floorSvgInfo.get("DRAW_MAP_ID").toString() + "," +
                                "\"LayerList\":" + objectToJson(floorSvgInfo.get("LayerList").toString()) + "," +
                                "\"SVGSRC\":" + objectToJson(floorSvgInfo.get("SVGSRC").toString()) + "}";
                    } else {
                        content = "{}";
                    }

                } else {
                    message = "获取当前位置失败！";
                }
            } else {
                message = "ap集合数据为空！";
            }
        } catch (Exception e) {
            message = "解析参数出错：" + "catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            return renderSuccessJson(content,false);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取附近场所接口
     */
    public String getMatchBuildingList(String jsonArrayObj) {

        String message = "";
        boolean flag = false;
        if (jsonArrayObj == null || "".equals(jsonArrayObj)) {
            message = "参数为空！";
            return renderErrorJson(message);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String content = "";
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            Double longitude = 0D;
            Double latitude = 0D;
            Double range;
            String keyWord = "";
            if (jsonObj.has("KEYWORD")) {
                // 过滤html
                keyWord = filterHtml(jsonObj.getString("KEYWORD").trim());
                // 转义单引号 ,ORACLE中查询一个单引号时要转成两个单引号
                keyWord = keyWord.replaceAll("'", "''");
                map.put("KEYWORD", keyWord);
            }
            if (jsonObj.has("LONGITUDE")) {
                longitude = jsonObj.getDouble("LONGITUDE");
                // 坐标值处理，让它与数据库相匹配
                longitude = SvgUtils.getEncodeLatLng(longitude);
                map.put("longitude", longitude);
            } else {
                message = "LONGITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("LATITUDE")) {
                latitude = jsonObj.getDouble("LATITUDE");
                // 坐标值处理，让它与数据库相匹配
                latitude = SvgUtils.getEncodeLatLng(latitude);
                map.put("latitude", latitude);
            } else {
                message = "LATITUDE属性值为空！";
                return renderErrorJson(message);
            }
            if (jsonObj.has("RANGE")) {
                // 有范围值时的查询条件
                range = jsonObj.getDouble("RANGE");
                range = SvgUtils.getEncodeLatLng(range / 111.2);
                map.put("range", range);
            }
            // 过滤状态无效的
            map.put("status", "X");
            // 附近匹配场所的数据
            List<Map<String, Object>> buildingList = apiDataMapper.getMatchBuildingList(map);
            // 把数据库的坐标值转成真实坐标值
            longitude = SvgUtils.getDecodeLatLng(longitude);
            latitude = SvgUtils.getDecodeLatLng(latitude);
            if (null != buildingList && !buildingList.isEmpty()) {
                String picWhere = "";
                // 场所图片信息的查询条件
                for (Map<String, Object> buildingMap : buildingList) {
                    if ("".equals(picWhere)) {
                        picWhere = picWhere + " (BUILDING_ID=" + buildingMap.get("BUILDING_ID");
                    } else {
                        picWhere = picWhere + " or BUILDING_ID=" + buildingMap.get("BUILDING_ID");
                    }
                }
                picWhere = picWhere + ") and FLOOR_ID is null";
                // 查询场所图片信息
                List<Map<String, Object>> buildingIconList = apiDataMapper.getPic(picWhere);
                String filePath = "";
                // 定义媒体库物理路径
                String medialibPath = System.getProperty("user.dir") + File.separator + "medialib/";
                for (Map<String, Object> buildingMap : buildingList) {
                    // 返回数据的生成
                    buildingMap.put("LT_LONGITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("LT_LONGITUDEL").toString())));
                    buildingMap.put("LT_LATITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("LT_LATITUDEL").toString())));
                    buildingMap.put("RB_LONGITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("RB_LONGITUDEL").toString())));
                    buildingMap.put("RB_LATITUDEL", SvgUtils.getDecodeLatLng(Double.parseDouble(buildingMap.get("RB_LATITUDEL").toString())));
                    double buildingLONGITUDEL = (Double.parseDouble(buildingMap.get("LT_LONGITUDEL").toString())
                            + Double.parseDouble(buildingMap.get("RB_LONGITUDEL").toString())) / 2;
                    double buildingLATITUDEL = (Double.parseDouble(buildingMap.get("LT_LATITUDEL").toString())
                            + Double.parseDouble(buildingMap.get("RB_LATITUDEL").toString())) / 2;
                    buildingMap.put("DISTANCE", SvgUtils.getLatLngDistance(latitude, longitude, buildingLATITUDEL, buildingLONGITUDEL) / 1000);
                    if (null != buildingIconList && !buildingIconList.isEmpty()) {
                        // 场所与图片信息相匹配
                        for (Map<String, Object> buildingIconMap : buildingIconList) {
                            if (buildingIconMap.get("BUILDING_ID").toString().equals(buildingIconMap.get("BUILDING_ID").toString())) {
                                filePath = medialibPath + buildingIconMap.get("PATH").toString() + "/" + buildingIconMap.get("FILENAME").toString() + "." +
                                        buildingIconMap.get("PIC_TYPE").toString();
                                if (new File(filePath).exists()) {
                                    buildingMap.put("BUILDING_ICON", Constants.SF_BASE_URL + "medialib/" + buildingIconMap.get("PATH") + "/" +
                                            buildingIconMap.get("FILENAME") + "." + buildingIconMap.get("PIC_TYPE"));
                                }
                            }
                        }
                    }
                }
                // 按DISTANCE从小到大排序
                buildingList.sort((map1, map2) -> map1.get("DISTANCE").toString().
                        compareTo(map2.get("DISTANCE").toString()));
                // 生成返回数据
                content = "{\"TOTALCOUNT\":" + buildingList.size() + ",\"NearbyBuildingList\":" + objectToJson(buildingList) + "}";
            } else {
                content = "{}";
            }
        } catch (Exception e) {
            message = "获取当前位置失败！ catch error:" + e.getMessage();
            log.error(message);
        }
        if ("".equals(message)){
            return renderSuccessJson(content,false);
        }
        return renderErrorJson(message);
    }

    /**
     * 获取楼层平面图数据
     */
    private Map<String, Object> getFloorSvg(Long drawMapId, Long buildingId, Long floorId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        String message = "";
        if (null != drawMapId) {
            result.put("DRAW_MAP_ID", drawMapId);
            result.put("SVGSRC", "");
            result.put("LayerList", Collections.EMPTY_LIST);
        } else {
            // 通过场所ID和楼层ID获取楼层平面图信息
            try {
                response = svgUtil.getSvg(buildingId, floorId, null);
                result.put("DRAW_MAP_ID", response.get("DRAW_MAP_ID"));
                result.put("SVGSRC", response.get("SVGSRC"));
                if ("".equals(response.get("SVGSRC").toString())) {
                    result.put("LayerList", Collections.EMPTY_LIST);
                } else {
                    result.put("LayerList", response.get("LayerList"));
                }
            } catch (ParserConfigurationException e) {
                message = "获取楼层平面图数据失败！ catch error:" + e.getMessage();
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                message = "获取楼层平面图数据失败！ catch error:" + e.getMessage();
                e.printStackTrace();
            } catch (TransformerException e) {
                message = "获取楼层平面图数据失败！ catch error:" + e.getMessage();
                e.printStackTrace();
            }

        }
        if ("".equals(message)) {
        } else {
            result.put("message", message);
        }
        return result;
    }

    /**
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     */
    private String filterHtml(String str) {
        // 过滤所有以<开头以>结尾的标签
        String regxpForHtml = "<([^>]*)>";
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String replace(String json){
      return json.replace("\"[","[").replace("]\"","]");
    }

    public static void main(String[] args) {

    }
}
