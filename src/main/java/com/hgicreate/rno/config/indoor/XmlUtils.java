package com.hgicreate.rno.config.indoor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析xml和生成xml
 */
@Slf4j
public class XmlUtils {

    private static final String title = "title";
    private static final String desc = "desc";
    private static final String width = "width";
    private static final String height = "height";

    public static Svg getSvgInfo(String xml) {

        // 将字符串转为XML
        Document document = null;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
            log.error("XML解析出现错误：{}", e.getMessage());
        }
        //获取根节点
        Element root = document.getRootElement();

        Map<String, Object> obj = new HashMap<String, Object>();

        List<SvgLayer> svgLayers = null;
        SvgLayer svgLayer = null;
        SvgElem svgElem = null;

        List<SvgElem> svgElems = null;
        Map<String, String> svgAttrs = null;

        Svg svg = new Svg();

        List<Attribute> svgList = root.attributes();
        svgList.forEach(a -> {
            if (width == a.getName()) {
                svg.setWidth(a.getValue());
            }
            if (height == a.getName()) {
                svg.setHeight(a.getValue());
            }
        });

        svgLayers = new ArrayList<SvgLayer>();
        List<Element> gLists = root.elements();
        for (Element layer : gLists) {
            List<Element> gList = layer.elements();
            //图层 跳过g
            svgElems = new ArrayList<SvgElem>();
            for (Element element : gList) {
                //元素
                if (title.equals(element.getName())) {
                    svgLayer = new SvgLayer();
                    svgLayer.setTitle(element.getStringValue());
                } else if (desc.equals(element.getName())) {
                    svgLayer.setDesc(element.getStringValue());
                    svgLayers.add(svgLayer);
                } else {
                    svgElem = new SvgElem();
                    svgElem.setElementName(element.getName());
                    if (!"".equals(element.getStringValue())) {
                        //该元素有值
                        svgElem.setElementVal(element.getStringValue());
                    }
                    svgAttrs = new HashMap<String, String>();
                    List<Attribute> listAttr = element.attributes();
                    for (Attribute attr : listAttr) {
                        //属性
                        String name = attr.getName();//属性名称
                        String value = attr.getValue();//属性的值
                        if (element.getName() != title || element.getName() != desc) {
                            svgAttrs.put(name, value);
                        }
                    }
                    svgElem.setSvgAttrs(svgAttrs);
                    //排除title与desc元素
                    svgElems.add(svgElem);
                }
                svgLayer.setSvgElems(svgElems);
            }
        }
        svg.setSvgLayers(svgLayers);
        return svg;
    }


    @Data
    static class Svg {
        String width;
        String height;
        List<SvgLayer> svgLayers;
    }

    @Data
    static class SvgLayer {
        String title;
        String desc;
        List<SvgElem> svgElems;
    }

    @Data
    static class SvgElem {
        String elementName;
        String elementVal;
        Map<String, String> svgAttrs;
    }

    public static void main(String[] args) throws DocumentException {

    }
}
