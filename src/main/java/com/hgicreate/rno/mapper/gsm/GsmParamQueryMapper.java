package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmParamQueryMapper {

    List<Map<String, Object>> queryBscListByCityId(int cityId);
    List<Map<String, Object>> queryDateListByCityId(int cityId, String dataType);

    List<Map<String, Object>> getCellParamRecord(GsmParamQueryVM vm);
    List<Map<String, Object>> getChannelParamRecord(GsmParamQueryVM vm);
    List<Map<String, Object>> getNcellParamRecord(GsmParamQueryVM vm);

}
