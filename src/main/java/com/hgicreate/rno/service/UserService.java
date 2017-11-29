package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id){
        return userRepository.findById(id);
    }

    public String saveUser(User user){
        userRepository.save(user);
        return "success";
    }
}
