package org.springboot.entity.dto;

import lombok.Data;

@Data
public class ExternalBookDTO {
    private String sourcePlatform;
    private String sourceId;
    private String name;
    private String author;
    private String imageUrl;
    private String tags;
    private String comment;
    private String isbn;
    private String publisher;
    private String publishYear;
    private String description;
    private Boolean existsLocal;
}
