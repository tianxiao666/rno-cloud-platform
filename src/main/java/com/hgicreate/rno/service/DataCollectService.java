package com.hgicreate.rno.service;

import com.hgicreate.rno.service.dto.DataCollectDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataCollectService {
    public List<DataCollectDTO> queryDataCollectDTOs() {
        List<DataCollectDTO> dtoList = new ArrayList<>();
        dtoList.add(new DataCollectDTO(1, "佛山", "2015-10-9 11:35:49",
                "路网通导出LTE数据.zip", "1366322", "2015-10-9 11:36:09",
                "2015-10-9 11:36:41", "liu.yp@iscreate.com", "部分失败"));
        dtoList.add(new DataCollectDTO(2, "佛山", "2015-9-28 18:18:41",
                "佛山网格数据业务采样点.csv", "19490250", "2015-9-28 18:19:03",
                "2015-9-28 18:19:35", "liu.yp@iscreate.com", "全部成功"));
        return dtoList;
    }
}
