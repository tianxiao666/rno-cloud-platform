package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;

import java.util.List;
import java.util.Map;

public interface GsmParamChangeService {
    List<Map<String, Object>> changeParamData(GsmParamChangeVM vm);
    List<Map<String, Object>> queryParamDeatialData(GsmParamChangeVM vm);
    Boolean queryDateExist(GsmParamChangeVM vm);
    List<Map<String, Object>> exportChangeParamData(GsmParamChangeVM vm);
}
