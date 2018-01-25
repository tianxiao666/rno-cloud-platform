package com.hgicreate.rno.config.indoor;

import com.hgicreate.rno.domain.indoor.*;
import com.hgicreate.rno.repository.indoor.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
@Transactional
public class SvgUtils {

    private final CbFloorRepository cbFloorRepository;
    private final DmDrawMapRepository dmDrawMapRepository;
    private final CDict cDict;
    private final DmPlaneLayerRepository dmPlaneLayerRepository;
    private final DmLayerElementRepository dmLayerElementRepository;
    private final DmLayerElementAttrRepository dmLayerElementAttrRepository;
    private final CbPicRepository cbPicRepository;
    private final ApEquipmentRepository apEquipmentRepository;
    private final CbPoiRepository cbPoiRepository;
    private final CbBuildingRepository cbBuildingRepository;

    public SvgUtils(CbFloorRepository cbFloorRepository, DmDrawMapRepository dmDrawMapRepository, CDict cDict,
                    DmPlaneLayerRepository dmPlaneLayerRepository, DmLayerElementRepository dmLayerElementRepository,
                    DmLayerElementAttrRepository dmLayerElementAttrRepository, CbPicRepository cbPicRepository, ApEquipmentRepository apEquipmentRepository,
                    CbPoiRepository cbPoiRepository, CbBuildingRepository cbBuildingRepository) {
        this.cbFloorRepository = cbFloorRepository;
        this.dmDrawMapRepository = dmDrawMapRepository;
        this.cDict = cDict;
        this.dmPlaneLayerRepository = dmPlaneLayerRepository;
        this.dmLayerElementRepository = dmLayerElementRepository;
        this.dmLayerElementAttrRepository = dmLayerElementAttrRepository;
        this.cbPicRepository = cbPicRepository;
        this.apEquipmentRepository = apEquipmentRepository;
        this.cbPoiRepository = cbPoiRepository;
        this.cbBuildingRepository = cbBuildingRepository;
    }

    private final String STATUS_NORMAL = "E";
    private final String STATUS_CANCEL = "X";
    /**
     * 赤道半径
     */
    private final static double EARTH_EQUATORIAL_RADIUS = 6378.1370;
    /**
     * 极半径
     */
    private final static double EARTH_POLAR_RADIUS = 6356.7523;

    /**
     * 编码经纬度
     */
    public static double getEncodeLatLng(double LatOrLng) {
        return (LatOrLng * 1e16);
    }

    /**
     * 解码经纬度
     */
    public static double getDecodeLatLng(double LatOrLng) {
        return (LatOrLng / 1e16);
    }

    public double getTransformViewValue(int number) {
        if (number == 0) {
            return 0;
        } else {
            return (number / 3600) / 1000;
        }
    }

    //  MSE 默认转换为毫秒 ， LAL 经纬度
    public double getTransformValue(double number, String type) {
        double val = 0;
        if ("MSE".equals(type)) {
            /* 只保留小数点后5位*/
            int position_before = Double.toString(number).indexOf(".");
            if (position_before != -1) {
                number = Double.parseDouble(Double.toString(number).substring(0, position_before + 6));

                double tmp = number * 3600 * 1000;
                int position = Double.toString(tmp).indexOf(".");
                if (position != -1) {
                    tmp = Double.parseDouble(Double.toString(tmp).substring(0, position));
                }
                val = Double.parseDouble(Double.toString(tmp).substring(0, 10));
            } else if ("LAL".equals(type)) {
                val = number / 3600000;
            }
        }
        return val;
    }

    /**
     * 弧度转换
     */
    public double rad(double num) {
        return num * Math.PI / 180.0;
    }

    /**
     * 度1°=111.31949079327
     *
     * @return $val @type m error -1
     * @lat1 lat2 纬度
     * @lng1 lng2 经度
     */
    public double GetDistance(double lng1, double lat1, double lng2, double lat2) {
        if ((Math.abs(lat1) > 90) || (Math.abs(lat2) > 90)) {
            return -1;
        }
        if ((Math.abs(lng1) > 180) || (Math.abs(lng2) > 180)) {
            return -1;
        }
        double radLat1 = this.rad(lat1);
        double radLat2 = this.rad(lat2);
        double a = this.rad(lat1) - this.rad(lat2);
        double b = this.rad(lng1) - this.rad(lng2);
        double val = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        val = val * 6378.137; // EARTH_RADIUS;
        //		val = round(val * 10000) / 10000;
        return val * 1000; //Unit:m
    }


    public double GetDistance(double lng1) {
        return this.GetDistance(1, 0, 0, 0);
    }

    /**
     * 返回1毫秒距离
     */
    public double getMsecByLngLat() {
        double LngLatM = this.GetDistance(1); //米
        return LngLatM / 3600000;
    }

    /**
     * 度1°=111.31949079327
     *
     * @return $val @type m
     * @lat1 $lat2 纬度
     * @lng1 $lng2 经度
     */
    public double GetSpotDistance(double lng1, double lat1, double lng2, double lat2) {
        if ((Math.abs(lat1) > 90) || (Math.abs(lat2) > 90)) {
            return -1;
        }
        if ((Math.abs(lng1) > 180) || (Math.abs(lng2) > 180)) {
            return -1;
        }
        double radLat1 = this.rad(lat1);
        double radLat2 = this.rad(lat2);
        double a = this.rad(lat1) - this.rad(lat2);
        double b = this.rad(lng1) - this.rad(lng2);

        double val = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        val = val * 6378137.0; // EARTH_RADIUS;
        val = Math.round(val * 10000) / 10000;
        return val;  //Unit:m
    }

    /**
     * 经纬比
     */
    public double[] getRatioLngLat(double lng1, double lat1, double lng2, double lat2, double ratio) {
        double[] lnglats = new double[2];
        if (ratio < 0 || ratio > 1) {
            lnglats[0] = -1;
            return lnglats;
        }
        double distance = this.GetDistance(lng1, lat1, lng2, lat2); //2点距离米
        double oppside = this.GetDistance(lng2, lat1, lng2, lat2); //对边距离
        double rad = Math.asin(oppside / distance);

        //距离转换为度数
        double lngLatM = this.GetDistance(1); //1度的距离。米
        double adis = distance * ratio; //距离A.米..

        double lngside = Math.sin(rad) * adis; //距离对边
        double latside = Math.cos(rad) * adis; //斜边

        lngside = lngside / lngLatM; //距离变回度数.
        latside = latside / lngLatM;
        double newlng = 0;
        double newlat = 0;
        if (lng1 >= lng2 && lat1 >= lat2) {
            newlng = lng1 - latside;
            newlat = lat1 - lngside;
        } else if (lng1 >= lng2 && lat1 <= lat2) {
            newlng = lng1 - latside;
            newlat = lat1 + lngside;
        } else if (lng1 <= lng2 && lat1 >= lat2) {
            newlng = lng1 + latside;
            newlat = lat1 - lngside;
        } else if (lng1 <= lng2 && lat1 <= lat2) {
            newlng = lng1 + latside;
            newlat = lat1 + lngside;
        }
        lnglats[0] = newlng;
        lnglats[1] = newlat;
        return lnglats;
    }

    /**
     * 下载SVG源代码
     */
    public Map<String, Object> getSvg(Long buildingId, Long floorId, Long drawMapId) throws ParserConfigurationException, UnsupportedEncodingException, TransformerException {
        Map<String, Object> response = new HashMap<String, Object>();
        String error = "";

        if (null == floorId) {
            if (null == buildingId) {
                error = "没有指定场所或楼层！";
            } else {
                List<CbFloor> cbFloors = cbFloorRepository.findByBuildingId(buildingId);
                floorId = cbFloors.get(0).getFloorId();
                if (null == cbFloors || cbFloors.size() == 0) {
                    error = "指定场所的楼层为空！";
                }
            }
        } else {
            if (null == buildingId) {
                CbFloor cbFloor = cbFloorRepository.findByFloorId(floorId);
                if (null == cbFloor || null == cbFloor.getBuildingId()) {
                    error = "找不到指定楼层所在的场所！";
                }
            }
        }

        if ("".equals(error)) {

            if (null == drawMapId) {
                List<DmDrawMap> dmDrawMaps = dmDrawMapRepository.findByFloorId(Long.toString(floorId));
                if (null != dmDrawMaps && dmDrawMaps.size() > 0) {
                    drawMapId = dmDrawMaps.get(0).getDrawMapId();
                }
            }
            if (null != drawMapId) {
                DmDrawMap dmDrawMap = dmDrawMapRepository.findByDrawMapId(drawMapId);
                response.put("FLOOR_ID", floorId);
                response.put("DRAW_MAP_ID", drawMapId);
                response.put("DW_UNIT", dmDrawMap.getDwUnit());
                response.put("DW_SCALE", dmDrawMap.getDwScale());
                response.put("STATUS", dmDrawMap.getStatus());
                response.put("DM_TOPIC", dmDrawMap.getDmTopic());

                response.put("STATUSNAME", cDict.BUILD_STATUS);

                //解析器工厂
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                //解析器
                DocumentBuilder db = dbf.newDocumentBuilder();
                //文档树模型
                Document dom = db.newDocument();
                Element svg = dom.createElement("svg");
                dom.appendChild(svg);
                svg.setAttribute("width", (dmDrawMap.getDwUnit() == "px") ? dmDrawMap.getWidth() : (dmDrawMap.getWidth() + dmDrawMap.getDwUnit()));
                svg.setAttribute("height", (dmDrawMap.getDwUnit() == "px") ? dmDrawMap.getHeight() : (dmDrawMap.getHeight() + dmDrawMap.getDwUnit()));
                svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
                svg.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");

                List<DmPlaneLayer> LayerFormList = dmPlaneLayerRepository.findByDrawMapIdAndStatusOrderByLOrder(Long.toString(drawMapId), this.STATUS_NORMAL);
                List<DmLayerElement> ElementFormList = dmLayerElementRepository.findByDrawMapIdAndStatusOrderByElementId(Long.toString(drawMapId), this.STATUS_NORMAL);

                //通过层ID存储元素集合[每个图层中包含n个元素]
                Map<String, List<DmLayerElement>> LayerElementFormList = new HashMap<String, List<DmLayerElement>>();
                ElementFormList.forEach(ElementForm -> {
                    List<DmLayerElement> dmLayerElementLists = LayerElementFormList.get(ElementForm.getLayerId());
                    if (dmLayerElementLists == null) {
                        dmLayerElementLists = new ArrayList<DmLayerElement>();
                    }
                    dmLayerElementLists.add(ElementForm);
                    LayerElementFormList.put(ElementForm.getLayerId(), dmLayerElementLists);
                });

                List<DmLayerElementAttr> ElementAttrFormList = dmLayerElementAttrRepository.findByDrawMapId(Long.toString(drawMapId));
                //通过元素ID存储元素属性集合[每个元素中包含n个属性名及属性值]
                Map<String, List<DmLayerElementAttr>> LayerElementAttrFormList = new HashMap<String, List<DmLayerElementAttr>>();
                if (null != ElementAttrFormList && ElementAttrFormList.size() != 0) {
                    ElementAttrFormList.forEach(ElementAttrForm -> {
                        List<DmLayerElementAttr> dmLayerElementAttrLists = LayerElementAttrFormList.get(ElementAttrForm.getElementId().toString());
                        if (dmLayerElementAttrLists == null) {
                            dmLayerElementAttrLists = new ArrayList<DmLayerElementAttr>();
                        }
                        dmLayerElementAttrLists.add(ElementAttrForm);
                        LayerElementAttrFormList.put(Long.toString(ElementAttrForm.getElementId()), dmLayerElementAttrLists);
                    });
                }
                Map<String, String> LayerList = new HashMap<String, String>();
                if (null != LayerFormList && LayerFormList.size() != 0) {
                    //图层创建
                    LayerFormList.forEach(LayerForm -> {
                        long layerId = LayerForm.getLayerId();
                        Element g = dom.createElement("g");
                        svg.appendChild(g);
                        Element title = dom.createElement("title");
                        g.appendChild(title);
                        Text title_value = null;
                        try {
                            title_value = dom.createTextNode(LayerForm.getLayerTopic());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        title.appendChild(title_value);
                        Element type = dom.createElement("desc");
                        g.appendChild(type);
                        Text type_value = dom.createTextNode(LayerForm.getLayerType());
                        type.appendChild(type_value);
                        List<DmLayerElement> LayerIdElementFormList = LayerElementFormList.get(LayerForm.getLayerId().toString());
                        //元素的创建
                        if (null != LayerIdElementFormList && LayerIdElementFormList.size() > 0) {
                            LayerIdElementFormList.forEach(LayerElementForm -> {
                                Element element = dom.createElement(LayerElementForm.getElementType().toLowerCase());
                                g.appendChild(element);
                                if (null != LayerElementForm.getElementText() && !"".equals(LayerElementForm.getElementText())) {
                                    Text element_html = null;
                                    try {
                                        element_html = dom.createTextNode(LayerElementForm.getElementText());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    element.appendChild(element_html);
                                }
                                List<DmLayerElementAttr> LayerElementIdAttrFormList = LayerElementAttrFormList.get(LayerElementForm.getElementId().toString());
                                //属性值的创建
                                if (null != LayerElementIdAttrFormList && LayerElementIdAttrFormList.size() > 0) {
                                    LayerElementIdAttrFormList.forEach(LayerElementAttrForm -> {
                                        element.setAttribute(LayerElementAttrForm.getAttrName().toLowerCase(), LayerElementAttrForm.getAttrValue());
                                    });
                                }
                            });
                        }
                        LayerList.put(LayerForm.getLayerType(), Long.toString(layerId));
                    });
                }
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty("version", "1.0");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                DOMSource source = new DOMSource(svg);
                StringWriter strReturn = new StringWriter();
                transformer.transform(source, new StreamResult(strReturn));

                String retString = strReturn.toString();
                response.put("SVGSRC", retString);
                // 取图层
                response.put("LayerList", LayerList);
            } else {
                error = "此楼层不存在SVG图片！";
            }
        }
        if (!"".equals(error)) {
            response.put("error", error);
        }
        return response;
    }

    /**
     * 上传SVG源代码
     */
    public Map<String, Object> ajaxUploadSvgAndSavePng(String paramsJson) throws JSONException {

        Map<String, Object> response = new HashMap<String, Object>();
        if (null == paramsJson || "".equals(paramsJson)) {
            return new HashMap<String, Object>();
        }
        //转换成为JSONObject对象
        JSONObject jsonObj = new JSONObject(paramsJson);

        Long buildingId = 0L;
        if (jsonObj.has("BUILDING_ID")) {
            buildingId = jsonObj.getLong("BUILDING_ID");
        }
        Long floorId = 0L;
        if (jsonObj.has("FLOOR_ID")) {
            floorId = jsonObj.getLong("FLOOR_ID");
        }
        Long drawMapId = null;
        if (jsonObj.has("DRAW_MAP_ID")) {
            if (!"".equals(jsonObj.getString("DRAW_MAP_ID"))) {
                drawMapId = jsonObj.getLong("DRAW_MAP_ID");
            }
        }
        Double dwScale = 0d;
        if (jsonObj.has("DW_SCALE")) {
            dwScale = jsonObj.getDouble("DW_SCALE");
        } else {
            dwScale = Double.parseDouble(cDict.DEFAULT_PLANEGRAPH.get("DW_SCALE"));
        }
        String backgroudColor = "";
        if (jsonObj.has("BACKGROUD_COLOR")) {
            backgroudColor = jsonObj.getString("BACKGROUD_COLOR");
        } else {
            backgroudColor = cDict.DEFAULT_PLANEGRAPH.get("BACKGROUD_COLOR");
        }
        String svgsrc = "";
        if (jsonObj.has("SVGSRC")) {
            svgsrc = jsonObj.getString("SVGSRC");
        }
        String pngbase64 = "";
        if (jsonObj.has("PNGBASE64")) {
            pngbase64 = jsonObj.getString("PNGBASE64");
        }
        String apFormListJsonStr = "";
        if (jsonObj.has("APFORMLISTJSONSTR")) {
            apFormListJsonStr = jsonObj.getString("APFORMLISTJSONSTR");
        }
        String poiFormListJsonStr = "";
        if (jsonObj.has("POIFORMLISTJSONSTR")) {
            poiFormListJsonStr = jsonObj.getString("POIFORMLISTJSONSTR");
        }
        String svgPicListJsonStr = "";
        if (jsonObj.has("SVGPICLISTJSONSTR")) {
            svgPicListJsonStr = jsonObj.getString("SVGPICLISTJSONSTR");
        }
        String dmTopic = jsonObj.getString("DM_TOPIC");

        JSONObject apFormListJson = null;
        if (!"".equals(apFormListJsonStr)) {
            apFormListJson = new JSONObject(apFormListJsonStr);
        }
        JSONObject poiFormListJson = null;
        if (!"".equals(poiFormListJsonStr)) {
            poiFormListJson = new JSONObject(poiFormListJsonStr);
        }
        JSONObject svgPicListJson = null;
        if (!"".equals(svgPicListJsonStr)) {
            svgPicListJson = new JSONObject(svgPicListJsonStr);
        }

        String error = "";
        String warning = "";
        if (null != floorId && !"".equals(svgsrc)) {

            if (null == drawMapId) {
                //获取下一个唯一标识ID
                ///  通过序列获取下一个标识
                drawMapId = dmDrawMapRepository.getSeqId();
            }
            if (null == buildingId) {
                CbFloor cbFloor = cbFloorRepository.findByFloorId(floorId);
                if (null == cbFloor) {
                    buildingId = 0L;
                } else {
                    buildingId = cbFloor.getBuildingId();
                }
            }
            String status = "";
            if (jsonObj.has("STATUS")) {
                jsonObj.getString("STATUS");
            } else {
                status = cDict.DEFAULT_PLANEGRAPH.get("STATUS");
            }

            String dwUnit = "";
            if (jsonObj.has("DW_UNIT")) {
                dwUnit = jsonObj.getString("DW_UNIT");
            } else {
                dwUnit = cDict.DEFAULT_PLANEGRAPH.get("DW_UNIT");
            }
            XmlUtils.Svg svgInfo = XmlUtils.getSvgInfo(svgsrc);
            DmDrawMap dmDrawMap = new DmDrawMap();
            List<DmPlaneLayer> layerFormList = new ArrayList<DmPlaneLayer>();
            List<DmLayerElement> elementFormList = new ArrayList<DmLayerElement>();
            List<DmLayerElementAttr> elementAttrFormList = new ArrayList<DmLayerElementAttr>();
            List<ApEquipment> apFormList = new ArrayList<ApEquipment>();
            List<CbPoi> poiFormList = new ArrayList<CbPoi>();
            CbBuilding cbBuilding = cbBuildingRepository.getOne(buildingId);
            //组装楼层平面图
            dmDrawMap.setBuildingId(buildingId);
            dmDrawMap.setFloorId(Long.toString(floorId));
            dmDrawMap.setDrawMapId(drawMapId);
            dmDrawMap.setDmTopic(dmTopic);
            dmDrawMap.setDwScale(Double.toString(dwScale));
            dmDrawMap.setDwUnit(dwUnit);
            dmDrawMap.setHeight(svgInfo.getHeight());
            dmDrawMap.setWidth(svgInfo.getWidth());
            dmDrawMap.setPicId("0");
            dmDrawMap.setDmNote("");
            dmDrawMap.setStatus(status);
            dmDrawMap.setModTime(getModTimeByCalendarDate());
            Map<String, String> svgLayerType = cDict.SVG_LAYER_TYPE;
            //平面图层
            List<XmlUtils.SvgLayer> layerInfos = svgInfo.getSvgLayers();
            int layerOrder = 0;
            if (null != layerInfos && layerInfos.size() > 0) {
                Long finalBuildingId = buildingId;
                JSONObject finalApFormListJson1 = apFormListJson;
                JSONObject finalPoiFormListJson1 = poiFormListJson;
                JSONObject finalSvgPicListJson1 = svgPicListJson;
                Long finalFloorId = floorId;
                Long finalDrawMapId = drawMapId;
//                layerInfos.forEach(svgLayer -> {
                for (XmlUtils.SvgLayer svgLayer : layerInfos) {
                    //组装平面图层
                    DmPlaneLayer layerForm = new DmPlaneLayer();
                    long layerID = dmPlaneLayerRepository.getSeqId();
                    layerForm.setBuildingId(Long.toString(finalBuildingId));
                    layerForm.setFloorId(Long.toString(finalFloorId));
                    layerForm.setDrawMapId(Long.toString(finalDrawMapId));
                    layerForm.setLayerNote("");
                    layerForm.setLOrder(Integer.toString(layerOrder));
                    layerForm.setLayerTopic(svgLayer.getTitle());
                    layerForm.setLayerType(svgLayer.getDesc());
                    layerForm.setLayerId(layerID);
                    if ("".equals(layerForm.getLayerTopic())) {
                        layerForm.setLayerTopic("NULL");
                    }
                    if ("".equals(layerForm.getLayerType())) {
                        layerForm.setLayerType("NULL");
                    }
                    ++layerOrder;
                    layerForm.setStatus("E");

                    //图层元素
                    List<XmlUtils.SvgElem> elementInfos = svgLayer.getSvgElems();
                    if (null != elementInfos && elementInfos.size() > 0) {
                        JSONObject finalApFormListJson = finalApFormListJson1;
                        JSONObject finalPoiFormListJson = finalPoiFormListJson1;
                        JSONObject finalSvgPicListJson = finalSvgPicListJson1;
//                        elementInfos.forEach(svgElem -> {
                        for (XmlUtils.SvgElem svgElem : elementInfos) {
                            //组装图层元素
                            DmLayerElement elementForm = new DmLayerElement();

                           /*XmlUtils.SvgAttr attr = svgElem.getSvgAttrs().stream().
                                   filter(svgAttr -> svgAttr.getAttrName().equals("id")).
                                   collect(Collectors.toList()).get(0);*/
                            Map<String, String> elementAttr = svgElem.getSvgAttrs();
                            long elementID = dmLayerElementRepository.getSeqId();
                            elementForm.setBuildingId(Long.toString(finalBuildingId));
                            elementForm.setFloorId(Long.toString(finalFloorId));
                            elementForm.setDrawMapId(Long.toString(finalDrawMapId));
                            elementForm.setLayerId(Long.toString(layerID));
                            elementForm.setElementId(elementID);
                            elementForm.setSvgId(elementAttr.get("id"));
                            elementForm.setElementTopic(elementAttr.get("id"));
                            elementForm.setElementType(svgElem.getElementName());
                            elementForm.setPoiId("");
                            elementForm.setPoiType("");
                            //元素中心坐标
                            Map<String, Double> position = SvgUtils.getSvgElementPosition(svgElem);
                            if (null == position || position.isEmpty()) {
                                position = new HashMap<String, Double>() {
                                    {
                                        put("X", 0d);
                                        put("Y", 0d);
                                    }
                                };
                            }
                            DecimalFormat df = new DecimalFormat("######0.00");
                            elementForm.setPositionX(df.format(position.get("X")));
                            elementForm.setPositionY(df.format(position.get("Y")));
                            elementForm.setStatus("E");
                            elementForm.setElementText(svgElem.getElementVal());
                            int oldlen = 0;
                            int maxbytes = 0;
                            int newlen = 0;
                            if (null != elementForm.getElementText()) {
                                oldlen = elementForm.getElementText().length();
                                maxbytes = 256;
                                elementForm.setElementText(SvgUtils.limitGbkStr(elementForm.getElementText(), maxbytes));
                                newlen = elementForm.getElementText().length();
                                if (newlen < oldlen) {
                                    warning = warning + "\n图层“" + layerForm.getLayerTopic() + "”中" + elementForm.getElementType() + "元素值有" + oldlen + "字节,只保存了" + newlen + "字节！";
                                    ;
                                }
                            }
                            if (layerForm.getLayerType() == svgLayerType.get("AP") && null != finalApFormListJson) {

                                JSONObject apJsons = new JSONObject(finalApFormListJson.getString(elementForm.getSvgId()));
                                ApEquipment apForm = new ApEquipment();
                                //组装AP定位数据
                                if (null != apJsons) {
                                    long apId = apEquipmentRepository.getSeqId();
                                    elementForm.setPoiType(svgLayerType.get("AP"));
                                    elementForm.setPoiId(Long.toString(apId));
                                    apForm.setApId(apId);
                                    apForm.setLayerId(Long.toString(layerID));
                                    apForm.setBuildingId(Long.toString(finalBuildingId));
                                    apForm.setFloorId(Long.toString(finalFloorId));
                                    apForm.setDrawMapId(Long.toString(finalDrawMapId));
                                    apForm.setSvgId(elementForm.getSvgId());
                                    apForm.setElementId(Long.toString(elementID));
                                    apForm.setPositionX(elementForm.getPositionX());
                                    apForm.setPositionY(elementForm.getPositionY());
                                    apForm.setEqutSsid(apJsons.getString("EQUT_SSID"));
                                    apForm.setEqutModel(apJsons.getString("EQUT_MODEL"));
                                    apForm.setNote(apJsons.getString("NOTE"));
                                    apForm.setMacBssid(apJsons.getString("MAC_BSSID").toUpperCase());
                                    //累加ap对象
                                    apFormList.add(apForm);
                                }
                            }

                            if (layerForm.getLayerType() == svgLayerType.get("POI") && null != finalPoiFormListJson) {
                                JSONObject poiJsons = new JSONObject(finalPoiFormListJson.getString(elementForm.getSvgId()));
                                CbPoi poiForm = new CbPoi();
                                //组装POI兴趣点数据
                                if (null != poiJsons) {
                                    long poiId = cbPoiRepository.getSeqId();

                                    elementForm.setPoiType(poiJsons.getString("POI_TYPE"));
                                    elementForm.setPoiId(Long.toString(poiId));

                                    poiForm.setPoiId(poiId);
                                    poiForm.setBuildingId(finalBuildingId);
                                    poiForm.setFloorId(Long.toString(finalFloorId));
                                    poiForm.setDrawMapId(Long.toString(finalDrawMapId));
                                    poiForm.setPicId("0");
                                    poiForm.setLayerId(Long.toString(layerID));
                                    poiForm.setSvgId(elementForm.getSvgId());
                                    poiForm.setElementId(Long.toString(elementID));
                                    poiForm.setPoiName(poiJsons.getString("POI_NAME"));
                                    poiForm.setPoiNote(poiJsons.getString("POI_NOTE"));
                                    poiForm.setCity(Long.toString(cbBuilding.getArea().getId()));
                                    poiForm.setProv(Long.toString(cbBuilding.getArea1().getId()));
                                    poiForm.setDistrict(cbBuilding.getDistrict());
                                    poiForm.setAddress(poiJsons.getString("ADDRESS"));
                                    poiForm.setPositionX(elementForm.getPositionX());
                                    poiForm.setPositionY(elementForm.getPositionY());
                                    poiForm.setNote(poiJsons.getString("NOTE"));
                                    /*if (null!= finalSvgPicListJson) {
                                      JSONObject  picJsons = new JSONObject(finalSvgPicListJson1.getString(elementForm.getSvgId()));
                                      if (null!=picJsons){
                                          CbPic picForm = new CbPic();
                                          picForm.setDrawMapId(Long.toString(drawMapId));
                                          picForm.setPoiId(Long.toString(poiForm.getPoiId()));
                                          if (null != picJsons.getString("NEW_PIC")) {
                                              if (false*//*! empty ( $_FILES )*//*) {
//                                                  $Poi_file = $_FILES [$ElementForm ["SVG_ID"]];
                                                  elementForm.getSvgId();
                                                  if (false*//*! empty ( $Poi_file )*//*) {
                                                      String buildingName = cbBuildingRepository.getBuildingNameByBuildingId(finalBuildingId);
                                                      String floorName = cbFloorRepository.getFloorNameByFloorId(floorId);
                                                      DmDrawMap drawMap = dmDrawMapRepository.findByDrawMapId(drawMapId);
                                                      String fileName = buildingName + "_" + floorName + "_" + dmDrawMap.getDmTopic() + "_" +poiForm.getPoiName()+ "_" + getTimeByCalendar();
                                                      *//*$PoiForm ["PIC_ID"] = SvgUtil::savePic ( $FILE_NAME, $PoiForm ["POI_ID"], $Poi_file, $FLOOR_ID, $BUILDING_ID, $DRAW_MAP_ID );
                                                      $PicForm ["PIC_ID"] = $PoiForm ["PIC_ID"];*//*
                                                  }
                                              }
                                          }
                                         *//* $PicInfo ["NEWPICFORM"] = $PicForm;
                                          $SVGPICLIST [$ElementForm ["SVG_ID"]] = $PicInfo;*//*
                                      }
                                    }*/
                                    //累加poi对象
                                    poiFormList.add(poiForm);
                                }
                            }
                            //累加图层元素对象
                            elementFormList.add(elementForm);
                            if (null != elementAttr && !elementAttr.isEmpty()) {
//                                elementAttr.forEach((attrName,attrValue)->{
                                for (String attrName : elementAttr.keySet()) {
                                    String attrValue = elementAttr.get(attrName);
                                    String attrVal = getSvgElementAttrValue(elementForm.getElementType(), attrName, attrValue);
                                    if (!"null".equals(attrVal)) {
                                        //组装图层元素属性对象
                                        DmLayerElementAttr elementAttrForm = new DmLayerElementAttr();
                                        elementAttrForm.setDrawMapId(Long.toString(finalDrawMapId));
                                        elementAttrForm.setLayerId(Long.toString(layerID));
                                        elementAttrForm.setElementId(elementID);
                                        elementAttrForm.setAttrName(attrName.toUpperCase());
                                        oldlen = attrVal.length();
                                        maxbytes = 4000;
                                        elementAttrForm.setAttrValue(limitGbkStr(attrVal, maxbytes));
                                        newlen = elementAttrForm.getAttrValue().length();
                                        if (newlen < oldlen) {
                                            warning = warning + "\n图层“" + layerForm.getLayerTopic() + "”中" + elementForm.getElementType() + "元素的" + attrName + "属性值有" + oldlen + "字节,只保存了" + newlen + "字节！";
                                        }
                                        //累加图层元素属性对象
                                        elementAttrFormList.add(elementAttrForm);
                                    }
                                }
                            }
                        }
                    }
                    //累加图层对象
                    layerFormList.add(layerForm);
                }
            }
            try {
                dmDrawMapRepository.save(dmDrawMap);

                dmPlaneLayerRepository.updateBuildingStatus(STATUS_CANCEL, Long.toString(drawMapId), STATUS_NORMAL);
                dmPlaneLayerRepository.save(layerFormList);

                dmLayerElementRepository.updateBuildingStatus(STATUS_CANCEL, Long.toString(drawMapId), STATUS_NORMAL);
                dmLayerElementRepository.save(elementFormList);

                dmLayerElementAttrRepository.deleteByDrawMapId(Long.toString(drawMapId));
                dmLayerElementAttrRepository.save(elementAttrFormList);

                apEquipmentRepository.deleteByDrawMapId(Long.toString(drawMapId));
                apEquipmentRepository.save(apFormList);

                cbPoiRepository.deleteByDrawMapId(Long.toString(drawMapId));
                cbPoiRepository.save(poiFormList);
            } catch (Exception e) {
                error = "执行数据库错误：";
                error = error + e.getMessage();
            }
        } else {
            error = "参数不正确！";
        }
        response.put("error", error);
        response.put("warning", warning);
        return response;
    }

    /**
     * 获取限制字节数字的GBK字符串
     */
    public static String limitGbkStr(String gbkStr, int limitBytes) {
        int strlen = gbkStr.length();
        if (strlen > limitBytes) {
            int counterOfDoubleByte = 0;
            String str = "";
            byte b[] = new byte[0];
            try {
                b = gbkStr.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (b.length <= limitBytes)
                return str;
            for (int i = 0; i < limitBytes; i++) {
                if (b[i] < 0)
                    counterOfDoubleByte++;
            }
            if (counterOfDoubleByte % 2 == 0) {
                try {
                    str = new String(b, 0, limitBytes, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    str = new String(b, 0, limitBytes - 1, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            ;
            return str;
        }
        return gbkStr;
    }

    private static String getTimeByCalendar() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对象
        Date date = new Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    private static String getModTimeByCalendar() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对象
        Date date = new Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private static Date getModTimeByCalendarDate() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对象
        Date date = new Date(l);
        //转换提日期输出格式
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return date;
    }

    /**
     * 角度转弧度
     */
    public static double radian(double angle) {
        return (angle * Math.PI / 180);
    }

    /**
     * 根据两点间的经纬度计算距离
     */
    public static double getLatLngDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = SvgUtils.radian(lat1);
        double radLat2 = SvgUtils.radian(lat2);
        double a = radLat1 - radLat2;
        double b = SvgUtils.radian(lng1) - SvgUtils.radian(lng2);
        double stepOne = Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2);
        double s = 2 * Math.asin(Math.sqrt(stepOne));
        s = s * (SvgUtils.EARTH_POLAR_RADIUS + SvgUtils.EARTH_EQUATORIAL_RADIUS) / 2;
        return Math.round(s * 100000) / 100;
    }

    /**
     * 创建补0的随机数
     */
    public static String randomAlignNumStr() {
        int min = 0, max = 99;
        Random random = new Random();
        int num = random.nextInt(100);
        int maxlen = Integer.toString(max).length();
        int numlen = Integer.toString(num).length();
        int count = maxlen - numlen;
        int i = 0;
        String zeroStr = "";
        while (i < count) {
            zeroStr = zeroStr + "0";
            ++i;
        }
        return (zeroStr + num);
    }

    /**
     * 创建随机目录
     */
    public static String createLevelPath() {
        int Level = 3;
        String path = "";
        int i = 0;
        while (i < Level) {
            String dir = randomAlignNumStr();
            path = (path == "") ? dir : (path + "/" + dir);
            ++i;
        }
        return (path);
    }

    /**
     * 获取场所PIC PATH
     */
    public String getPicPathByBuilding(Long buildingId) {

        String path = cbPicRepository.findDistinctPathByBuildingIdAndPathNotNull(Long.toString(buildingId));
        while ("".equals(path)) {
            path = createLevelPath();
            path = cbPicRepository.findDistinctPathByPath(path);
            if (!"".equals(path)) {
                path = null;
            }
        }
        return (path);
    }

    public static String mkdirs(String root, String dirs) {
        File file = new File(root);
        boolean dir_exists = file.exists();
        String errormsg = "";
        if (!dir_exists) {
            try {
                dir_exists = file.mkdirs();
            } catch (Exception e) {
                errormsg = "创建目录\"" + root + "\"失败！";
                errormsg = errormsg + e.getMessage();
                return errormsg;
            }
        }
        if (dir_exists) {
            root = file.getParent();
            String[] pathList = dirs.split(File.separator);
            for (String v : pathList) {
                root = root + File.separator + v;
                if (!new File(root).exists()) {
                    try {
                        dir_exists = new File(root).mkdirs();
                    } catch (Exception e) {
                        errormsg = "创建目录\"" + root + "\"失败！";
                        errormsg = errormsg + e.getMessage();
                        return errormsg;
                    }
                }
            }
            return "{'path':" + root + ",'dirs':" + dirs + "}";
        } else {
            return "目录\"" + root + "\"不存在！";
        }
    }

    /**
     * 获取SVG元素中心坐标
     */
    public static Map<String, Double> getSvgElementPosition(XmlUtils.SvgElem elementInfo) {
        Map<String, Double> position = new HashMap<String, Double>();
        if (null != elementInfo) {
            int elementId = getSvgElementId(elementInfo.getElementName().toUpperCase());
            Map<String, String> elementAttrs = elementInfo.getSvgAttrs();
            switch (elementId) {
                case SVG_ELEMENT_ID_RECT:
                case SVG_ELEMENT_ID_IMAGE:
                    position.put("X", Double.parseDouble(elementAttrs.get("X".toLowerCase())) + Double.parseDouble(elementAttrs.get("WIDTH".toLowerCase())) / 2);
                    position.put("Y", Double.parseDouble(elementAttrs.get("Y".toLowerCase())) + Double.parseDouble(elementAttrs.get("HEIGHT".toLowerCase())) / 2);
                    break;
                case SVG_ELEMENT_ID_CIRCLE:
                case SVG_ELEMENT_ID_ELLIPSE:
                    position.put("X", Double.parseDouble(elementAttrs.get("CX".toLowerCase())));
                    position.put("Y", Double.parseDouble(elementAttrs.get("CY".toLowerCase())));
                    break;
                case SVG_ELEMENT_ID_LINE:
                    position.put("X", (Double.parseDouble(elementAttrs.get("X1".toLowerCase())) + Double.parseDouble(elementAttrs.get("X2".toLowerCase()))) / 2);
                    position.put("Y", (Double.parseDouble(elementAttrs.get("Y1".toLowerCase())) + Double.parseDouble(elementAttrs.get("Y2".toLowerCase()))) / 2);
                    break;
                case SVG_ELEMENT_ID_POLYGON:
                case SVG_ELEMENT_ID_POLYLINE:
                case SVG_ELEMENT_ID_PATH:
                    String points = elementAttrs.get(((elementId == SVG_ELEMENT_ID_PATH) ? "D" : "POINTS").toLowerCase()).trim();
                    points = points.replace("  ", " ");
                    points = points.replace(" ,", ",");
                    points = points.replace(", ", ",");
                    double sumx = 0;
                    double sumy = 0;
                    int pointCount = 0;
                    switch (elementId) {
                        default:
                            String[] pointsArr = points.split(" ");
                            for (String coord : pointsArr) {
                                if (!"".equals(coord)) {
                                    String[] arr = coord.split(",");
                                    if (arr.length == 2) {
                                        sumx = sumx + Double.parseDouble(arr[0]);
                                        sumy = sumy + Double.parseDouble(arr[1]);
                                        ++pointCount;
                                    }
                                }
                            }
                            break;
                        case SVG_ELEMENT_ID_PATH:
                            String positionX = "";
                            String positionY = "";
                            String pathTag = "";
                            points = points + " ";
                            int pointsLen = points.length();
                            double lastX = 0;
                            double lastY = 0;
                            int i = 0;
                            while (i < pointsLen) {
                                char ch = points.charAt(i);
                                Integer id = getSvgElementPathAttrId(String.valueOf(ch).toUpperCase());
                                if (id != null) {
                                    if (!"".equals(pathTag)) {
                                        if (!pathTag.equals(pathTag.toUpperCase())) {
                                            if ("".equals(positionX)) {
                                                positionX = Double.toString(lastX);
                                            } else {
                                                positionX = (lastX + Double.parseDouble(positionX)) + "";
                                            }
                                            if (null == positionY || "".equals(positionY)) {
                                                positionY = Double.toString(lastY);
                                            } else {
                                                positionY = (lastY + Double.parseDouble(positionY)) + "";
                                            }
                                        }
                                        sumx = sumx + Double.parseDouble(positionX);
                                        sumy = sumy + Double.parseDouble(positionY);
                                        ++pointCount;
                                    }
                                    if (id != SVG_ELEMENT_PATH_ID_X) {
                                        pathTag = String.valueOf(ch);
                                        if ("".equals(positionX)) {
                                            lastX = 0;
                                        } else {
                                            lastX = Double.parseDouble(positionX);
                                        }
                                        if ("".equals(positionY)) {
                                            lastY = 0;
                                        } else {
                                            lastY = Double.parseDouble(positionY);
                                        }
                                    }
                                    positionX = "";
                                    positionY = null;
                                } else {
                                    if (!"".equals(pathTag)) {
                                        if (",".equals(String.valueOf(ch))) {
                                            positionY = "";
                                        } else {
                                            if (null == positionY) {
                                                positionX = positionX + String.valueOf(ch);
                                            } else {
                                                positionY = positionY + String.valueOf(ch);
                                            }
                                        }
                                    }
                                }
                                ++i;
                            }
                            break;
                    }
                    if (pointCount > 0) {
                        position.put("X", sumx / pointCount);
                        position.put("Y", sumy / pointCount);
                    }
                    break;
                case SVG_ELEMENT_ID_TEXT:
                    position.put("X", Double.parseDouble(elementAttrs.get("X".toLowerCase())));
                    position.put("Y", Double.parseDouble(elementAttrs.get("Y".toLowerCase())));
                    break;
                default:break;
            }
        }
        return (position);
    }

    /**
     * SVG形状ID
     */
    private static final int SVG_ELEMENT_ID_RECT = 0;
    private static final int SVG_ELEMENT_ID_CIRCLE = 1;
    private static final int SVG_ELEMENT_ID_ELLIPSE = 2;
    private static final int SVG_ELEMENT_ID_LINE = 3;
    private static final int SVG_ELEMENT_ID_POLYGON = 4;
    private static final int SVG_ELEMENT_ID_POLYLINE = 5;
    private static final int SVG_ELEMENT_ID_PATH = 6;
    private static final int SVG_ELEMENT_ID_TEXT = 7;
    private static final int SVG_ELEMENT_ID_IMAGE = 8;
    /**
     * SVG Path属性
     */
    private static final int SVG_ELEMENT_PATH_ID_M = 0;
    private static final int SVG_ELEMENT_PATH_ID_L = 1;
    private static final int SVG_ELEMENT_PATH_ID_H = 2;
    private static final int SVG_ELEMENT_PATH_ID_V = 3;
    private static final int SVG_ELEMENT_PATH_ID_C = 4;
    private static final int SVG_ELEMENT_PATH_ID_S = 5;
    private static final int SVG_ELEMENT_PATH_ID_Q = 6;
    private static final int SVG_ELEMENT_PATH_ID_T = 7;
    private static final int SVG_ELEMENT_PATH_ID_A = 8;
    private static final int SVG_ELEMENT_PATH_ID_Z = 9;
    private static final int SVG_ELEMENT_PATH_ID_X = 10;

    /**
     * 获取SVG形状ID
     */
    public static int getSvgElementId(String svgElementTagName) {

        Map<String, Integer> svgElementMap = new HashMap<String, Integer>() {
            {
                put("RECT", SVG_ELEMENT_ID_RECT);
                put("CIRCLE", SVG_ELEMENT_ID_CIRCLE);
                put("ELLIPSE", SVG_ELEMENT_ID_ELLIPSE);
                put("LINE", SVG_ELEMENT_ID_LINE);
                put("POLYGON", SVG_ELEMENT_ID_POLYGON);
                put("POLYLINE", SVG_ELEMENT_ID_POLYLINE);
                put("PATH", SVG_ELEMENT_ID_PATH);
                put("TEXT", SVG_ELEMENT_ID_TEXT);
                put("IMAGE", SVG_ELEMENT_ID_IMAGE);
            }
        };
        return svgElementMap.get(svgElementTagName);
    }

    /**
     * 获取SVG Path属性ID
     */
    public static Integer getSvgElementPathAttrId(String svgElementPathAttrName) {

        Map<String, Integer> svgElementPathAttrMap = new HashMap<String, Integer>() {
            {
                put("M", SVG_ELEMENT_PATH_ID_M);
                put("L", SVG_ELEMENT_PATH_ID_L);
                put("H", SVG_ELEMENT_PATH_ID_H);
                put("V", SVG_ELEMENT_PATH_ID_V);
                put("C", SVG_ELEMENT_PATH_ID_C);
                put("S", SVG_ELEMENT_PATH_ID_S);
                put("Q", SVG_ELEMENT_PATH_ID_Q);
                put("T", SVG_ELEMENT_PATH_ID_T);
                put("A", SVG_ELEMENT_PATH_ID_A);
                put("Z", SVG_ELEMENT_PATH_ID_Z);
                put(" ", SVG_ELEMENT_PATH_ID_X);
            }
        };
        return svgElementPathAttrMap.get(svgElementPathAttrName);
    }

    /**
     * 获取真实的属性值
     */
    public static String getSvgElementAttrValue(String elementType, String attrName, String attrValue) {

        Map<String, Map<String, String>> notEssential = new HashMap<String, Map<String, String>>() {
            {
                put("STROKE", new HashMap<String, String>() {{
                    put("null", "false");
                }});
                put("STROKE-WIDTH", new HashMap<String, String>() {{
                    put("null", "false");
                    put("1", "false");
                }});
                put("STROKE-DASHARRAY", new HashMap<String, String>() {{
                    put("null", "false");
                }});
                put("STROKE-LINEJOIN", new HashMap<String, String>() {{
                    put("null", "false");
                }});
                put("STROKE-LINECAP", new HashMap<String, String>() {{
                    put("null", "false");
                }});
                put("FILL", new HashMap<String, String>() {{
                    put("black", "false");
                    put("null", "false");
                }});
            }
        };
        Map<String, String> notEssentialAttrValue = notEssential.get(attrName.toUpperCase());
        if (null != notEssentialAttrValue) {
            String realattrvalue = notEssentialAttrValue.get(attrValue.toLowerCase());
            if (null != realattrvalue) {
                if ("false".equals(realattrvalue)) {
                    realattrvalue = "null";
                }
                return (realattrvalue);
            }
        }
        return (attrValue);
    }

    public static void main(String[] args) {

    }


}

