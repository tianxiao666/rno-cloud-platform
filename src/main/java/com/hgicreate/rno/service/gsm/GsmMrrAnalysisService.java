package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrAnalysisQueryVM;

import java.util.List;
import java.util.Map;

public interface GsmMrrAnalysisService {
    Map<String, List<Map<String, Object>>> queryAllBscByAreaId(String areaId);
    List<Map<String, Object>> queryEriMrrData(GsmMrrAnalysisQueryVM vm);
}
