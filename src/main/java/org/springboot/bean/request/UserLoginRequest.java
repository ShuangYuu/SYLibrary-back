package org.springboot.bean.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String phone;
    private String password;
}
