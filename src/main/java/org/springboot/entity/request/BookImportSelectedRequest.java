package org.springboot.entity.request;

import lombok.Data;
import org.springboot.entity.dto.ExternalBookDTO;

import java.util.List;

@Data
public class BookImportSelectedRequest {
    private String source = "openLibrary";
    private List<ExternalBookDTO> books;
}
