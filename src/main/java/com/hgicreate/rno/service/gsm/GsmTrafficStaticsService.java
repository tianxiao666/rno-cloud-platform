package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmStsResultVM;

import java.util.List;
import java.util.Map;

public interface GsmTrafficStaticsService {

    List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(final String configIds);

    List<GsmStsResultVM> staticsResourceUtilizationRateInSelList(String stsCode, List<Integer> selectedList);

    List<GsmStsResultVM> staticsSpecialCellInSelList(String stsCode, List<Integer> selConfigs);
}
