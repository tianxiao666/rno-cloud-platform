package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.service.dto.AppDTO;
import com.hgicreate.rno.service.mapper.AppMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class AppService {
    private final AppRepository appRepository;

    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public List<String> getAllName(){
        List<String> result = new ArrayList<>();
        List<App> apps = appRepository.findAll();
        for (App app: apps) {
            result.add(app.getName());
        }
        return result;
    }

    public AppDTO getAppByName(String name){
        List<App> apps = appRepository.findByName(name);
        return AppMapper.INSTANCE.appToAppDTO(apps.get(0));
    }

    public String updateApp(App app){
        appRepository.save(app);
        return "success";
    }
}
