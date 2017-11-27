package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.service.dto.AppDTO;
import com.hgicreate.rno.service.dto.AppNameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppMapper {

    AppMapper INSTANCE = Mappers.getMapper( AppMapper.class );

    @Mapping(source = "id", target = "appId")
    @Mapping(source = "code", target = "appCode")
    @Mapping(source = "name", target = "appName")
    @Mapping(source = "version", target = "appVersion")
    @Mapping(source = "logo", target = "appLogo")
    @Mapping(source = "description", target = "appDescription")
    @Mapping(source = "style", target = "appStyle")
    @Mapping(source = "status", target = "appStatus")
    AppDTO appToAppDTO(App app);

    @Mapping(source = "id", target = "appId")
    @Mapping(source = "code", target = "appCode")
    @Mapping(source = "name", target = "appName")
    AppNameDTO appToAppNameDTO(App app);
}
