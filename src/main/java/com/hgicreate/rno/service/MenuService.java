package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AppRepository appRepository;

    @Value("${rno.app-code}")
    private String app_code;

    public List<Menu> getAllInfo() {
        List<App> appCode = appRepository.findAllByCode(app_code);
        return menuRepository.findAllByParentIdIsAndAppIdIsOrderByIndexOfBrother(0L, appCode.get(0).getId());
    }

    public void delAll() {
        List<App> appCode = getAllByAppCode();
        menuRepository.deleteAllByAppId(appCode.get(0).getId());
    }

    public void saveData(Menu menu) {
        menuRepository.save(menu);
    }

    public List<App> getAllByAppCode(){
        return appRepository.findAllByCode(app_code);
    }
}
