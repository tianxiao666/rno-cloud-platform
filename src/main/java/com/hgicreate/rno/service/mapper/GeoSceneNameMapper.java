package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.GeoScene;
import com.hgicreate.rno.service.dto.GeoSceneNameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface GeoSceneNameMapper {

    GeoSceneNameMapper INSTANCE = Mappers.getMapper( GeoSceneNameMapper.class );

    GeoSceneNameDTO sceneToGeoSceneNameDTO(GeoScene geoScene);
}
