package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.config.indoor.CDict;
import com.hgicreate.rno.config.indoor.SvgUtils;
import com.hgicreate.rno.domain.indoor.*;
import com.hgicreate.rno.repository.indoor.*;
import com.hgicreate.rno.service.indoor.dto.DmPlaneLayerDTO;
import com.hgicreate.rno.service.indoor.mapper.DmPlaneLayerMapper;
import com.hgicreate.rno.web.rest.indoor.vm.DmPlaneLayerDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DmPlaneLayerService {

    private final DmPlaneLayerRepository dmPlaneLayerRepository;
    private final TypeCodeRepository typeCodeRepository;
    private final CbBuildingRepository cbBuildingRepository;
    private final CbFloorRepository cbFloorRepository;
    private final DmDrawMapRepository dmDrawMapRepository;
    private final CbPoiRepository cbPoiRepository;
    private final CbPicRepository cbPicRepository;
    private final ApEquipmentRepository apEquipmentRepository;
    private final CDict cDict;

    public DmPlaneLayerService(DmPlaneLayerRepository dmPlaneLayerRepository, TypeCodeRepository typeCodeRepository, CbBuildingRepository cbBuildingRepository, CbFloorRepository cbFloorRepository, DmDrawMapRepository dmDrawMapRepository, CbPoiRepository cbPoiRepository, CbPicRepository cbPicRepository, ApEquipmentRepository apEquipmentRepository, CDict cDict) {
        this.dmPlaneLayerRepository = dmPlaneLayerRepository;
        this.typeCodeRepository = typeCodeRepository;
        this.cbBuildingRepository = cbBuildingRepository;
        this.cbFloorRepository = cbFloorRepository;
        this.dmDrawMapRepository = dmDrawMapRepository;
        this.cbPoiRepository = cbPoiRepository;
        this.cbPicRepository = cbPicRepository;
        this.apEquipmentRepository = apEquipmentRepository;
        this.cDict = cDict;
    }

    public List<DmPlaneLayerDTO> dmPlaneLayerQuery(DmPlaneLayerDataQueryVM vm){
        return dmPlaneLayerRepository.findByDrawMapId(vm.getDrawMapId()).
                stream().
                map(DmPlaneLayerMapper.INSTANCE::dmPlaneLayerToDmPlaneLayerDTO).
                collect(Collectors.toList());
    }

    public Map<String, Object> renderSvgInterface(DmPlaneLayerDataQueryVM vm){
        Map<String, Object> pageData = new HashMap<String, Object>();
        List<TypeCode> typeList = typeCodeRepository.findAll();

        final String[] typeOption = {""};
        Map<String, Object> typeArray = new HashMap<String, Object>();
        if (!"".equals(typeList)&& typeList.size()>0) {
            typeList.forEach(typeCode->{
                typeOption[0] = typeOption[0] + "<option id=\\'typecode_" + typeCode.getCodeType() + "\\' value=\\'" + typeCode.getCodeType() + "\\'>" + typeCode.getCodeName() + "</option>";
                try {
                    typeArray.put(typeCode.getCodeType(), typeCode.getCodeName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            typeOption[0] = "<option value=\\'\\'>无类别</option>";
        }
        String a = "-请选择-";
        String layerList = "<li onmouseover=\\'showSecondMenu(this);\\' class=\\'\\' id=\\'\\' onchange=\\'newLayer(this);\\'><select><option id=\\'\\' value=\\'\\'>-请选择-</option>" +typeOption[0]+ "</select><div class=\\'tool_sep\\' style=\\'float:right;\\'></div><div class=\\'\\' style=\\'color:#000000;float:right;\\'><a href=\\'#\\' onclick=\\'delLayer(this);\\'>-</a></div></li>";
        String layerSel = "toilet";
        pageData.put("layerSel",layerSel);
        pageData.put("layerList",layerList);
        pageData.put("typeOption",typeOption[0]);
        pageData.put("typeList",typeList);
        pageData.put("typeJson",typeArray);

        if (null!=vm.getBuildingId()&&!"".equals(vm.getBuildingId())) {
            List<CbFloor> floorList = cbFloorRepository.findByBuildingId(Long.parseLong(vm.getBuildingId()));
            pageData.put("floorList",floorList);
            pageData.put("BUILDING_ID",vm.getBuildingId());
            CbBuilding cbBuilding = null;
            if (null==vm.getBuildingName() || "".equals(vm.getBuildingName())) {
                cbBuilding = cbBuildingRepository.findByBuildingId(Long.parseLong(vm.getBuildingId()));
                pageData.put("buildingList",cbBuilding);
                if ("".equals(cbBuilding.getBuildingName())) {
                    vm.setBuildingName("无法显示场所名(ID:"+vm.getBuildingId()+")");
                }else {
                    vm.setBuildingName(cbBuilding.getBuildingName());
                }
            }
            pageData.put("BUILDING_NAME",vm.getBuildingName());
            if (null!=cbBuilding) {
                double lt_lat = SvgUtils.getDecodeLatLng(cbBuilding.getLtLatitudel());
                double lt_lng = SvgUtils.getDecodeLatLng(cbBuilding.getLtLongitudel());
                double rb_lat = SvgUtils.getDecodeLatLng (cbBuilding.getRbLatitudel());
                double rb_lng = SvgUtils.getDecodeLatLng (cbBuilding.getRbLongitudel());
                pageData.put("FLOOR_WIDTH", SvgUtils.getLatLngDistance( lt_lat, lt_lng, lt_lat, rb_lng ));
                pageData.put("FLOOR_HEIGHT", SvgUtils.getLatLngDistance(lt_lat, lt_lng, rb_lat, lt_lng ));
            }
        }
        if (null!=vm.getDrawMapId()&&!vm.getDrawMapId().isEmpty()) {
            DmDrawMap PlanegraphInfo = dmDrawMapRepository.findByDrawMapId(Long.parseLong(vm.getDrawMapId()));
            pageData.put("PlanegraphInfo",PlanegraphInfo);
            pageData.put("FLOOR_ID",PlanegraphInfo.getFloorId());
            pageData.put("DRAW_MAP_ID",vm.getDrawMapId());
            List<CbPoi> result = cbPoiRepository.findByDrawMapId(vm.getDrawMapId());
            if (null!=result) {
                Map<String,Object> PoiFormList = new HashMap<String,Object>();
                Map<String,Object> PoiSvgList = new HashMap<String,Object>();
                final String[] poiIds = {""};
                result.forEach(PoiForm->{
                  String SVG_ID = PoiForm.getSvgId();
                  Long POI_ID = PoiForm.getPoiId();
                  PoiSvgList.put(Long.toString(POI_ID),SVG_ID);
                    poiIds[0] = poiIds[0] + POI_ID+ ",";
                    PoiFormList.put(SVG_ID,PoiForm);
                });

                if (!PoiFormList.isEmpty()) {
                    pageData.put("POIFORMLISTJSONSTR",PoiFormList);
                }
                List<CbPic> resultCbPic = null;
                if (!"".equals(poiIds[0])){
                    poiIds[0] = poiIds[0].substring(0, poiIds[0].lastIndexOf(','));
                    resultCbPic = cbPicRepository.findByPoiIdInAndStatusNotContaining(poiIds[0],"X");
                }

                if (null!=resultCbPic && !resultCbPic.isEmpty()){
                    Map<String,Map<String,Object>> SvgPicList = new HashMap<String,Map<String,Object>>();
                    resultCbPic.forEach(cbPic -> {
                        PoiSvgList.get(cbPic.getPoiId());
                        SvgPicList.put( PoiSvgList.get(cbPic.getPoiId()).toString(),new HashMap<String,Object>(){
                                {
                                   put("PIC_ID",cbPic.getPicId());
                                }
                        });
                    });
                    if (!SvgPicList.isEmpty()) {
                        pageData.put("SVGPICLISTJSONSTR",SvgPicList);
                    }
                }
            }

            List<ApEquipment> resultApEquipment = apEquipmentRepository.findByDrawMapId(vm.getDrawMapId());
            if (!resultApEquipment.isEmpty()) {
                Map<String,Object> ApFormList = new HashMap<String,Object>();
                resultApEquipment.forEach(apEquipment -> {
                    String SVG_ID = apEquipment.getSvgId();
                    ApFormList.put(SVG_ID,apEquipment);
                });
                if (!ApFormList.isEmpty()) {
                    pageData.put("APFORMLISTJSONSTR",ApFormList);
                }
            }
        }
        if (null!=vm.getFloorId()&&!"".equals(vm.getFloorId())) {
            pageData.put("FLOOR_ID",vm.getFloorId());
        }
        pageData.put("DEFAULT_PLANEGRAPH",cDict.DEFAULT_PLANEGRAPH);
        pageData.put("BUILD_STATUS",cDict.BUILD_STATUS);
        pageData.put("PLANEGRAPH_UNIT",cDict.PLANEGRAPH_UNIT);
        pageData.put("POI_STATUS",cDict.POI_STATUS);
        pageData.put("POI_TYPE",cDict.POI_TYPE);
        pageData.put("EQUT_STATUS",cDict.EQUT_STATUS);
        pageData.put("EQUT_TYPE",cDict.EQUT_TYPE);
        pageData.put("EQUT_BRANDS",cDict.EQUT_BRANDS);
        pageData.put("EQUT_FACTORY",cDict.EQUT_FACTORY);
        pageData.put("ANT_FREQUENCYS",cDict.ANT_FREQUENCYS);
        pageData.put("SVG_LAYER_TYPE",cDict.SVG_LAYER_TYPE);

        return pageData;
    }
}
