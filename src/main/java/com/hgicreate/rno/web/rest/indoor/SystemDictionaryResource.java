package com.hgicreate.rno.web.rest.indoor;

import com.hgicreate.rno.config.indoor.CDict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/sys-dict-data")
public class SystemDictionaryResource {

    private final CDict cDict;

    public SystemDictionaryResource(CDict cDict) {
        this.cDict = cDict;
    }

    @GetMapping("/get-ap-sys-dict-info")
    public Map<String,Object> getApSysDictInfo(){
        Map<String,Object> obj = new HashMap<String,Object>();
        obj.put("EQUT_FACTORY",cDict.EQUT_FACTORY);
        obj.put("EQUT_BRANDS",cDict.EQUT_BRANDS);
        obj.put("EQUT_TYPE",cDict.EQUT_TYPE);
        obj.put("EQUT_STATUS",cDict.EQUT_STATUS);
        return obj;
    }

    @GetMapping("/get-poi-sys-dict-info")
    public Map<String,Object> getPoiSysDictInfo(){
        Map<String,Object> obj = new HashMap<String,Object>();
        obj.put("POI_TYPE",cDict.POI_TYPE);
        obj.put("POI_STATUS",cDict.POI_STATUS);
        return obj;
    }
}
