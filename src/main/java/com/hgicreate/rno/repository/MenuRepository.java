package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Menu;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface MenuRepository extends CrudRepository<Menu, Integer> {

    List<Menu> findAllByParentIdIsOrderByIndexOfBrother(Long pid);

    @Override
    <S extends Menu> S save(S s);

    @Modifying
    @Transactional
    @Query("delete from Menu")
    void delAll();
}
