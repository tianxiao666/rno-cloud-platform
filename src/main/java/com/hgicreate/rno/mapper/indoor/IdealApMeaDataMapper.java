package com.hgicreate.rno.mapper.indoor;

import com.hgicreate.rno.domain.indoor.IdealApMeaDataInfo;
import com.hgicreate.rno.web.rest.indoor.vm.IdealApMeaDataQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IdealApMeaDataMapper {

    /**
     *查询理想AP测量数据
     */
    public List<IdealApMeaDataInfo> queryIdealApMeaData(IdealApMeaDataQueryVM vm);
}
