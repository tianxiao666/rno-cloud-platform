package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.service.dto.AppDTO;
import com.hgicreate.rno.service.dto.AppNameDTO;
import com.hgicreate.rno.service.mapper.AppMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppService {
    private final AppRepository appRepository;

    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public List<AppNameDTO> getAllName(){
        return appRepository.findAll().stream().map(AppMapper.INSTANCE::appToAppNameDTO).collect(Collectors.toList());
    }

    public AppDTO getAppById(Long id){
        return AppMapper.INSTANCE.appToAppDTO(appRepository.findById(id));
    }

    public Long updateApp(App app){
        appRepository.save(app);
        Long id = appRepository.findAllByCode(app.getCode()).get(0).getId();
        System.out.println("id = " + id);
        return id;
    }

    public String deleteAppById(Long id){
        appRepository.delete(id);
        return "success";
    }
}
