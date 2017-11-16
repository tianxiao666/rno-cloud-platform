package com.hgicreate.rno.service;


import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> getAllInfo(){
        return menuRepository.findAllByParentIdIsOrderByIndexOfBrother(0L);
    }

    public void delAll(){
        menuRepository.delAll();
    }

    public void saveData(Menu menu){
        menuRepository.save(menu);
    }
}
