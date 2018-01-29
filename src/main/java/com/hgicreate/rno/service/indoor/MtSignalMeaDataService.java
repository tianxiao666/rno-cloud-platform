package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.domain.indoor.MtSignalMeaData;
import com.hgicreate.rno.domain.indoor.MtSignalMeaDataInfo;
import com.hgicreate.rno.mapper.indoor.MtSignalMeaDataMapper;
import com.hgicreate.rno.repository.indoor.MtSignalMeaDataRepository;
import com.hgicreate.rno.service.indoor.dto.MtSignalMeaDataInfoDTO;
import com.hgicreate.rno.web.rest.indoor.vm.MtSignalMeaDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class MtSignalMeaDataService {

    private final MtSignalMeaDataMapper mtSignalMeaDataDaoMapper;
    private final MtSignalMeaDataRepository mtSignalMeaDataRepository;

    public MtSignalMeaDataService(MtSignalMeaDataMapper mtSignalMeaDataDaoMapper, MtSignalMeaDataRepository mtSignalMeaDataRepository) {
        this.mtSignalMeaDataDaoMapper = mtSignalMeaDataDaoMapper;
        this.mtSignalMeaDataRepository = mtSignalMeaDataRepository;
    }

    public List<MtSignalMeaDataInfo> queryMtSignalMeaData(MtSignalMeaDataQueryVM vm) {
        return mtSignalMeaDataDaoMapper.queryMtSignalMeaData(vm);
    }

    public List<MtSignalMeaDataInfoDTO> queryMtSignalMeaDataDTOs(MtSignalMeaDataQueryVM vm) {
        return mtSignalMeaDataDaoMapper.queryMtSignalMeaData(vm).
                stream().
                map(com.hgicreate.rno.service.indoor.mapper.MtSignalMeaDataMapper.INSTANCE::mtSignalMeaDataInfoToMtSignalMeaDataInfoDTO).
                collect(Collectors.toList());
    }

    public boolean saveMtSignalMeaData(String jsonArrayObj){

        if (jsonArrayObj==null || "".equals(jsonArrayObj)) return false;
        MtSignalMeaData mtSignalMeaData = new MtSignalMeaData();
        try {
            // 转换成为JSONObject对象
            JSONObject jsonObj = new JSONObject(jsonArrayObj);
            mtSignalMeaData.setBuildingId(jsonObj.getString("BUILDING_ID"));
            mtSignalMeaData.setFloorId(jsonObj.getString("FLOOR_ID"));
            mtSignalMeaData.setDrawMapId(jsonObj.getString("DRAW_MAP_ID"));
            mtSignalMeaData.setSignal(jsonObj.getString("SIGNAL"));
            mtSignalMeaData.setLatitude(jsonObj.getString("LATITUDE"));
            mtSignalMeaData.setLongitude(jsonObj.getString("LONGITUDE"));
            mtSignalMeaData.setDeviceId(jsonObj.getString("DEVICE_ID"));
            mtSignalMeaData.setPlaneX(jsonObj.getString("PLANE_X"));
            mtSignalMeaData.setPlaneY(jsonObj.getString("PLANE_Y"));
            mtSignalMeaData.setSignalType(jsonObj.getString("SIGNAL_TYPE"));

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateNowStr = sdf.format(d);

            mtSignalMeaData.setMeaDate(dateNowStr);

            mtSignalMeaDataRepository.save(mtSignalMeaData);
        }catch (Exception e){
            log.debug("mtSignalMeaData时出现异常，exception={}",e);
            return false;
        }finally {
            log.debug("saveIdealApMeaData数据属性，mtSignalMeaData={}",mtSignalMeaData.toString());
            mtSignalMeaData =null;
        }
        return true;
    }
}
