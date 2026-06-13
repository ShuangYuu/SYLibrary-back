package org.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserFavoriteBook {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Timestamp createdTime;
}
