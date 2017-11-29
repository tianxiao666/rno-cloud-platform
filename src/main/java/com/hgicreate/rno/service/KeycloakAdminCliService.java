package com.hgicreate.rno.service;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.security.SecurityUtils;
import org.apache.catalina.security.SecurityUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakAdminCliService {

    @Value("${keycloak.auth-server-url}")
    private String SERVER_URL;

    @Value("${keycloak.realm}")
    private String REALM;

    private Keycloak getInstance() {
        return KeycloakBuilder
                .builder()
                .serverUrl(SERVER_URL)
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId(Constants.ADMIN_CLI_CLIENT_ID)
                .build();
    }

    /**
     * 更新 Keycloak 认证中心的用户信息
     * @param user 用户对象
     * @return 成功返回true，失败返回false
     */
    boolean updateAccount(final User user) {
        UsersResource usersResource = getInstance().realm(REALM).users();
        List<UserRepresentation> userRepresentationList = usersResource.search(user.getUsername());
        if (userRepresentationList.size() > 0) {
            UserRepresentation userRepresentation = userRepresentationList.get(0);
            userRepresentation.setEmail(user.getEmail());
            userRepresentation.setFirstName(user.getFullName());
            usersResource.get(userRepresentation.getId()).update(userRepresentation);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 重置密码
     */
    void resetPassword(String newPassword) {
        // 设置新密码
        CredentialRepresentation newCredential = new CredentialRepresentation();
        UserResource userResource = getInstance().realm(REALM).users().get(SecurityUtils.getAccessToken().getId());
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(newPassword);
        newCredential.setTemporary(false);
        userResource.resetPassword(newCredential);
    }
}
