package org.springboot.entity.request;

import lombok.Data;

@Data
public class ExternalBookSearchRequest {
    private String source = "openLibrary";
    private String keyword;
    private Integer limit = 20;
    private Integer page = 1;
    private String language;
    private Boolean onlyWithCover = false;
    private Boolean onlyWithDescription = false;
    private Boolean skipExisting = false;
}
