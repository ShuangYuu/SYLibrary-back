package org.springboot.bean;

import lombok.Data;

@Data
public class Book {
    private String book_id;
    private String name;
    private String author;
    private String imageUrl;
    private String tags;
    private String comment;
}
