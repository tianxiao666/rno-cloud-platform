package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.ParamCheckVM;

import java.util.List;
import java.util.Map;

public interface ParamCheckService {
    List<Map<String, Object>> queryParamData(ParamCheckVM vm);
}
