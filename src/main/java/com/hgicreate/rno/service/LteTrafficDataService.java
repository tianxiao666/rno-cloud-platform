package com.hgicreate.rno.service;

import com.hgicreate.rno.service.dto.LteTrafficDataDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LteTrafficDataService {
    public List<LteTrafficDataDTO> queryTrafficDataCollectDTOs() {
        List<LteTrafficDataDTO> dtoList = new ArrayList<>();
        dtoList.add(new LteTrafficDataDTO("广州市", "2015-9-15 15:20:56", "广州市天河区20150716-14-30_15-15_LTE话统数据.zip",
                "205.82M", "2015-9-15 15:21:52", "2015-9-15 15:24:58",
                "liu.yp@iscreate.com", "全部成功"));
        return dtoList;
    }
}
