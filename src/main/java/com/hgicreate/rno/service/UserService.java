package com.hgicreate.rno.service;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.repository.UserRepository;
import com.hgicreate.rno.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    private final UserRepository userRepository;
    private final CasUserService casUserService;

    public UserService(UserRepository userRepository, CasUserService casUserService) {
        this.userRepository = userRepository;
        this.casUserService = casUserService;
    }

    public User getCurrentUser() {
        String username = SecurityUtils.getCurrentUserLogin();
        List<User> userList = userRepository.findByUsername(username);
        return userList.get(0);
    }

    public boolean saveUser(User user) {
        boolean isSuccess = casUserService.updateAccount(user);
        if (isSuccess) {
            user.setLastModifiedDate(new Date());
            user.setLastModifiedUser(user.getUsername());
            userRepository.save(user);
        }
        return isSuccess;
    }

    public String checkUser() {
        List<User> userList = userRepository.findByUsername(SecurityUtils.getCurrentUserLogin());
        //如果用户信息已存在数据库
        if (userList != null && !userList.isEmpty()) {
            String message = "更新了";
            User user = userList.get(0);
            String fullName = SecurityUtils.getFullName();
            String email = SecurityUtils.getEmail();
            //判断fullName和email是否相同，如果不同，则更新，更新人为system
            if (!fullName.equals(user.getFullName())) {
                user.setFullName(fullName);
                user.setLastModifiedUser(Constants.SYSTEM_ACCOUNT);
                message = message + "fullName.";
            }
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
                user.setLastModifiedUser(Constants.SYSTEM_ACCOUNT);
                message = message + "email";
            }
            return message;
        } else {
            //从数据库中新建一个用户
            User newUser = new User();
            newUser.setEmail(SecurityUtils.getEmail());
            newUser.setFullName(SecurityUtils.getFullName());
            newUser.setUsername(SecurityUtils.getCurrentUserLogin());
            newUser.setCreatedUser(Constants.SYSTEM_ACCOUNT);
            Date date = new Date();
            newUser.setCreatedDate(date);
            newUser.setLastModifiedUser(Constants.SYSTEM_ACCOUNT);
            newUser.setLastModifiedDate(date);
            newUser.setDefaultArea(Constants.DEFAULT_AREA);
            userRepository.save(newUser);
            return "新建了" + SecurityUtils.getCurrentUserLogin();
        }
    }

}
