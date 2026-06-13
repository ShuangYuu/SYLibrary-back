package org.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    private Integer id;
    private String username;
    private String userImage;
    private String password;
    private String cardID;
    private Integer age;
    private String sex;
    private String phone;
    private String address;
    private Timestamp createdTime;
    private Timestamp updateTime;
    private String role = "USER";
}
