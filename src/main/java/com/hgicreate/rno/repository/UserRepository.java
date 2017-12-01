package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ke_weixu
 */
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);
}
