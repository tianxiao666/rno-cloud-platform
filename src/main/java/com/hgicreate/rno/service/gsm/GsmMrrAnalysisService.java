package com.hgicreate.rno.service.gsm;

import java.util.List;
import java.util.Map;

public interface GsmMrrAnalysisService {
    Map<String, List<Map<String, Object>>> queryAllBscByAreaId(String areaId);
}
