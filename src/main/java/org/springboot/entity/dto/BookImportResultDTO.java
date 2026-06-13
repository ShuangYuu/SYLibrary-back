package org.springboot.entity.dto;

import lombok.Data;

@Data
public class BookImportResultDTO {
    private int selected;
    private int requested;
    private int fetched;
    private int imported;
    private int duplicated;
    private int failed;
}
