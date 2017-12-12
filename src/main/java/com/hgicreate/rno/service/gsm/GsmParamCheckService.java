package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;

import java.util.List;
import java.util.Map;

public interface GsmParamCheckService {
    List<Map<String, Object>> checkParamData(GsmParamCheckVM vm);
}
