package org.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Timestamp createdTime;
    private Timestamp updateTime;
    private String role = "ADMIN";
}
