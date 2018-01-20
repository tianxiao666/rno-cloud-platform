package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmMrrAnalysisMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrAnalysisQueryVM;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zeng.dh1
 */

@Service
public class GsmMrrAnalysisServiceImpl implements GsmMrrAnalysisService {

    private final GsmMrrAnalysisMapper gsmMrrAnalysisMapper;

    public GsmMrrAnalysisServiceImpl(GsmMrrAnalysisMapper gsmMrrAnalysisMapper) {
        this.gsmMrrAnalysisMapper = gsmMrrAnalysisMapper;
    }

    @Override
    public Map<String, List<Map<String, Object>>> queryAllBscByAreaId(String areaId) {
        Map<String, List<Map<String, Object>>> res = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> cells = gsmMrrAnalysisMapper.queryAllBscByAreaId(Long.parseLong(areaId));
        String prebsc = "", curbsc = "";
        List<Map<String, Object>> line = null;
        for (Map<String, Object> one : cells) {
            if (one == null) {
                continue;
            }
            if (one.get("ENGNAME") == null) {
                curbsc = "未知bsc";
            } else {
                curbsc = "" + one.get("ENGNAME");
            }
            if (!prebsc.equals(curbsc)) {
                if (line != null && !line.isEmpty()) {
                    res.put(prebsc, line);
                }
                line = new ArrayList<Map<String, Object>>();
                prebsc = curbsc;
            }
            line.add(one);
        }

        if (!line.isEmpty()) {
            res.put(prebsc, line);
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> queryEriMrrData(GsmMrrAnalysisQueryVM vm) {
        if(("Rxlev").equals(vm.getMrrDataType())) {
            vm.setTableName("RNO_GSM_MRR_STRENGTH");
        } else if(("RxQual").equals(vm.getMrrDataType())) {
            vm.setTableName("RNO_GSM_MRR_QUALITY");
        } else if(("POWER").equals(vm.getMrrDataType())) {
            vm.setTableName("RNO_GSM_MRR_POWER");
        } else if(("PATHLOSS").equals(vm.getMrrDataType())) {
            vm.setTableName("RNO_GSM_MRR_PL");
        } else if(("PLDIFF").equals(vm.getMrrDataType())) {
            vm.setTableName("RNO_GSM_MRR_PLD");
        } else if(("TA").equals(vm.getMrrDataType())) {
            vm.setTableName("RNO_GSM_MRR_TA");
        }else {
            return new ArrayList<>();
        }
        return gsmMrrAnalysisMapper.queryAllMrrData(vm);
    }


}
