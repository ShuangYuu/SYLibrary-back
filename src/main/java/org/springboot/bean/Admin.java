package org.springboot.bean;

import lombok.Data;
import org.apache.tomcat.util.bcel.Const;

import java.sql.Timestamp;

@Data
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String role = "ADMIN";
}
