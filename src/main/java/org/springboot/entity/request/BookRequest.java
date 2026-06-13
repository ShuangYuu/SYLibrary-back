package org.springboot.entity.request;

import lombok.Data;

@Data
public class BookRequest extends BaseRequestPage {
    private String name;
    private String author;
    private String tags;
}
