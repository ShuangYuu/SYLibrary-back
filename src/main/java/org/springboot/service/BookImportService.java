package org.springboot.service;

import org.springboot.entity.dto.BookImportResultDTO;
import org.springboot.entity.dto.ExternalBookDTO;
import org.springboot.entity.request.BookImportSelectedRequest;
import org.springboot.entity.request.ExternalBookSearchRequest;

import java.util.List;

public interface BookImportService {

    List<ExternalBookDTO> searchExternalBooks(ExternalBookSearchRequest request);

    BookImportResultDTO importSelectedBooks(BookImportSelectedRequest request);

    BookImportResultDTO importOpenLibraryBooks(ExternalBookSearchRequest request);
}
