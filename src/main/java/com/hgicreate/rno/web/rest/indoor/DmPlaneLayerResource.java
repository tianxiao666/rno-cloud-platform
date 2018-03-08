package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.config.indoor.SvgUtils;
import com.hgicreate.rno.service.indoor.DmPlaneLayerService;
import com.hgicreate.rno.service.indoor.dto.DmPlaneLayerDTO;
import com.hgicreate.rno.web.rest.indoor.vm.DmPlaneLayerDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/dm-plane-Layer-data")
public class DmPlaneLayerResource {

    private final DmPlaneLayerService dmPlaneLayerService;
    private final SvgUtils svgUtil;
    public DmPlaneLayerResource(DmPlaneLayerService dmPlaneLayerService, SvgUtils svgUtil) {
        this.dmPlaneLayerService = dmPlaneLayerService;
        this.svgUtil = svgUtil;
    }

    @PostMapping("/dm-plane-layer-query-by-drawmapid")
    public List<DmPlaneLayerDTO> dmPlaneLayerQuery(DmPlaneLayerDataQueryVM vm){
        log.debug("进入视图资源dmPlaneLayerQuery 查询平面图层数据,vm={}",vm);
        return dmPlaneLayerService.dmPlaneLayerQuery(vm);
    }

    @PostMapping("/ajax-download-svg")
    public Map<String, Object> ajaxDownLoadSvg(DmPlaneLayerDataQueryVM vm){
        log.debug("进入视图资源ajaxDownLoadSvg 下载SVG数据,vm={}",vm);
        Map<String, Object> xmlObj = null;
        try{
            xmlObj =  svgUtil.getSvg(Long.parseLong(vm.getBuildingId()),Long.parseLong(vm.getFloorId()),Long.parseLong(vm.getDrawMapId()));
        }catch (IOException e){
            e.printStackTrace();
            log.error("ajaxDownLoadSvg err={}",e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
            log.error("ajaxDownLoadSvg err={}",e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            log.error("ajaxDownLoadSvg err={}",e.getMessage());
        }
        return xmlObj;
    }

    @PostMapping("/render-svg-interface")
    public Map<String, Object> renderSvgInterface(DmPlaneLayerDataQueryVM vm){
        log.debug("进入视图资源renderSvgInterface 展现svg界面,vm={}",vm);
        Map<String, Object> xmlObj = null;
        xmlObj = dmPlaneLayerService.renderSvgInterface(vm);
        return xmlObj;
    }

    @PostMapping("/upload-svg-and-save-png")
    public Map<String, Object> ajaxUploadSvgAndSavePng(@RequestParam("PARAMSJSON") String paramsJson){
        log.debug("进入视图资源ajaxUploadSvgAndSavePng 保存svg信息,共收到 paramsJson={} 个字节！",paramsJson.length());
        Map<String, Object> xmlObj = null;
        try {
            xmlObj = svgUtil.ajaxUploadSvgAndSavePng(paramsJson);
        } catch (JSONException e) {
            e.printStackTrace();
            log.error("ajaxUploadSvgAndSavePng catch error:"+e.getMessage());
        }
        return xmlObj;
    }

}
