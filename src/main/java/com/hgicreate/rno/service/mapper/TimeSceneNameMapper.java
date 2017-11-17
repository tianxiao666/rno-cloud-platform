package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.GeoScene;
import com.hgicreate.rno.domain.TimeScene;
import com.hgicreate.rno.service.dto.GeoSceneNameDTO;
import com.hgicreate.rno.service.dto.TimeSceneNameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TimeSceneNameMapper {

    TimeSceneNameMapper INSTANCE = Mappers.getMapper( TimeSceneNameMapper.class );

    TimeSceneNameDTO sceneToTimeSceneNameDTO(TimeScene timeScene);
}
