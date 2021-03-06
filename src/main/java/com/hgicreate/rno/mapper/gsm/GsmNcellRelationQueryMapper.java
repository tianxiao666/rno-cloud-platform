package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.domain.gsm.GsmNcell;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcellRelationQueryVM;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmNcellRelationQueryMapper {
    /**
     * 查询邻区关系
     * @param vm 邻区关系vm
     * @return 邻区关系列表
     */
    List<GsmNcell> queryNcellRelation(GsmNcellRelationQueryVM vm);
}
