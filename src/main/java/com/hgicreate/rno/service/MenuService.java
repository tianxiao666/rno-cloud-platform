package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final AppRepository appRepository;

    public MenuService(MenuRepository menuRepository, AppRepository appRepository) {
        this.menuRepository = menuRepository;
        this.appRepository = appRepository;
    }

    public List<Menu> getAllInfo(String code) {
        List<App> list = appRepository.findAllByCode(code);
        return menuRepository.findAllByParentIdIsAndAppIdIsOrderByIndexOfBrother(0L, list.get(0).getId());
    }

    public void delelteAllByAppId(Long appId) {
        menuRepository.deleteAllByAppId(appId);
    }

    public void saveData(Menu menu) {
        menuRepository.save(menu);
    }
}
