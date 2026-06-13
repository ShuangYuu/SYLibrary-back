package org.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Book {
    private String book_id;
    private String name;
    private String author;
    private String imageUrl;
    private String tags;
    private String comment;
    private String sourcePlatform;
    private String sourceId;
    private String isbn;
    private String publisher;
    private String publishYear;
    private String description;
    private Timestamp createdTime;
}
