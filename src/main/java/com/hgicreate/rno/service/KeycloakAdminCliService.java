package com.hgicreate.rno.service;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
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

    public boolean updateAccount(final User user) {
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
}
