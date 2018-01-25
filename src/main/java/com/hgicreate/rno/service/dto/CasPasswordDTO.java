package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CasPasswordDTO {
    private String username;
    private String password;
    private String oldPassword;
}
