package com.hgicreate.rno.mapper.indoor;

import com.hgicreate.rno.domain.indoor.MtSignalMeaDataInfo;
import com.hgicreate.rno.web.rest.indoor.vm.MtSignalMeaDataQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MtSignalMeaDataMapper {

    /**
     * 查询移动终端测量数据
     */
    public List<MtSignalMeaDataInfo> queryMtSignalMeaData(MtSignalMeaDataQueryVM vm);
}
