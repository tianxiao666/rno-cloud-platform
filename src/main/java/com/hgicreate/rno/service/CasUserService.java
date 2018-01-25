package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.dto.CasPasswordDTO;
import com.hgicreate.rno.service.dto.CasUserAttrDTO;
import com.hgicreate.rno.service.mapper.CasUserAttrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wuhb
 */
@Slf4j
@Service
public class CasUserService {

    private final Environment env;

    public CasUserService(Environment env) {
        this.env = env;
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 更新CAS服务器中的用户信息
     * @param user 用户
     * @return 是否成功
     */
    public boolean updateAccount(User user) {
        // TODO 更新CAS服务器中的用户信息
        log.debug("User = {}", user);
        CasUserAttrDTO dto = CasUserAttrMapper.INSTANCE.areaToAreaDTO(user);
        log.debug("CasUserAttrDTO = {}", dto);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CasUserAttrDTO> entity = new HttpEntity<>(dto, headers);

        String endpoint = env.getProperty("security.cas.server.base-url","");

        if (endpoint.length() > 0) {
            endpoint += "/api/user-management/update-user-attr";
            ResponseEntity<String> response = restTemplate().exchange(endpoint, HttpMethod.PUT, entity, String.class);
            return Boolean.valueOf(response.getBody());
        } else {
            return false;
        }
    }

    public boolean resetPassword(String oldPassword, String newPassword) {
        CasPasswordDTO casPasswordDTO = new CasPasswordDTO(SecurityUtils.getCurrentUserLogin(), newPassword, oldPassword);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CasPasswordDTO> entity = new HttpEntity<>(casPasswordDTO, headers);

        String endpoint = env.getProperty("security.cas.server.base-url","");

        if (endpoint.length() > 0) {
            endpoint += "/api/user-management/update-password";
            ResponseEntity<String> response = restTemplate().exchange(endpoint, HttpMethod.PUT, entity, String.class);
            return Boolean.valueOf(response.getBody());
        } else {
            return false;
        }

    }
}
