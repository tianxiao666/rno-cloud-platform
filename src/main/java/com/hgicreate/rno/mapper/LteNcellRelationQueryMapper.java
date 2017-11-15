package com.hgicreate.rno.mapper;

import com.hgicreate.rno.domain.NcellRelation;
import com.hgicreate.rno.web.rest.vm.LteNcellRelationQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LteNcellRelationQueryMapper {
    /**
     * 查询邻区关系
     * @return 邻区关系列表
     */
    public List<NcellRelation> queryNcellRelation(LteNcellRelationQueryVM vm);
}
