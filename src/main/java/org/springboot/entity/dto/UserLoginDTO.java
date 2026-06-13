package org.springboot.entity.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String phone;
    private String password;
    private String code;
}
