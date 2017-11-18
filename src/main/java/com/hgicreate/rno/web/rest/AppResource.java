package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.service.dto.AreaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class AppResource {

    @Value("${rno.app-code:rno}")
    private String code;

    private final AppRepository appRepository;

    public AppResource(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @GetMapping("/app-info")
    public App getAppInfo() {
        return appRepository.findAllByCode(code).get(0);
    }
}
