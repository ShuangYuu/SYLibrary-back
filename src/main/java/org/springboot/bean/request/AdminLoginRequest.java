package org.springboot.bean.request;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String username;
    private String password;
}
