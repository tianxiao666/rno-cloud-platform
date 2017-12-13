package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DynamicCoverageResult {
    private List<Map<String, Object>> vectorPoint;
    private List<Map<String, Object>> curvePoints;
    private List<DynamicCoverageData> resInterDetail;
}
