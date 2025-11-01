package org.springboot.bean.request;

import lombok.Data;

@Data
public class BookRequest extends BaseRequestPage{
    private String name;
    private String author;
    private String ISBN;
}
