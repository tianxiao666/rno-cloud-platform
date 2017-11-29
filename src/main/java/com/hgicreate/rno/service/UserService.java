package com.hgicreate.rno.service;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.repository.UserRepository;
import com.hgicreate.rno.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;
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
    private final KeycloakAdminCliService keycloakAdminCliService;

    public UserService(UserRepository userRepository, KeycloakAdminCliService keycloakAdminCliService) {
        this.userRepository = userRepository;
        this.keycloakAdminCliService = keycloakAdminCliService;
    }

    public User getCurrentUser(){
        AccessToken token = SecurityUtils.getAccessToken();
        String username = token.getPreferredUsername();
        List<User> userList = userRepository.findByUsername(username);
        return userList.get(0);
    }

    public boolean saveUser(User user){
        boolean info = keycloakAdminCliService.updateAccount(user);
        if (info){
            user.setLastModifiedDate(new Date());
            user.setLastModifiedUser(user.getUsername());
            userRepository.save(user);
            AccessToken token = SecurityUtils.getAccessToken();
            token.setEmail(user.getEmail());
            token.setGivenName(user.getFullName());
            return true;
        }
        return false;
    }

    public String checkUser(){
        AccessToken token = SecurityUtils.getAccessToken();
        List<User> userList = userRepository.findByUsername(token.getPreferredUsername());
        //如果用户信息已存在数据库
        if (userList != null && !userList.isEmpty()){
            String message = "更新了";
            User user = userList.get(0);
            //判断fullName和email是否相同，如果不同，则更新，更新人为system
            if (token.getGivenName() == null || !token.getGivenName().equals(user.getFullName())){
                user.setFullName(token.getGivenName());
                user.setLastModifiedUser(Constants.SYSTEM_ACCOUNT);
                message = message + "fullName.";
            } if (token.getEmail() == null || !token.getEmail().equals(user.getEmail())){
                user.setEmail(token.getEmail());
                user.setLastModifiedUser(Constants.SYSTEM_ACCOUNT);
                message = message + "email";
            }
            return message;
        }else{
            //从数据库中新建一个用户
            User newUser = new User();
            newUser.setEmail(token.getEmail());
            newUser.setFullName(token.getGivenName());
            newUser.setUsername(token.getPreferredUsername());
            newUser.setCreatedUser(Constants.SYSTEM_ACCOUNT);
            Date date = new Date();
            newUser.setCreatedDate(date);
            newUser.setLastModifiedUser(Constants.SYSTEM_ACCOUNT);
            newUser.setLastModifiedDate(date);
            newUser.setDefaultArea(Constants.DEFAULT_AREA);
            userRepository.save(newUser);
            return "新建了" + token.getPreferredUsername();
        }
    }

}
