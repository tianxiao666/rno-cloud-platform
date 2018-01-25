package com.hgicreate.rno.config;

import com.kakawait.spring.boot.security.cas.CasSecurityConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class WebSecurityConfig extends CasSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 解决 iframe 的权限问题
        http.headers().frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable().and().csrf().disable();
    }
}
