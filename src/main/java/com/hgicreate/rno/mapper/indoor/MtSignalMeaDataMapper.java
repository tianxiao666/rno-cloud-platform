package com.hgicreate.rno.mapper.indoor;

import com.hgicreate.rno.domain.indoor.MtSignalMeaDataInfo;
import com.hgicreate.rno.web.rest.indoor.vm.MtSignalMeaDataQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chao.xj
 */
@Mapper
public interface MtSignalMeaDataMapper {

    /**
     * 查询移动终端测量数据
     * @param vm
     * @return 移动终端信号采集数据信息
     */
    List<MtSignalMeaDataInfo> queryMtSignalMeaData(MtSignalMeaDataQueryVM vm);
}
