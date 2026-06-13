package org.springboot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springboot.entity.Book;

import java.util.List;

@Data
@AllArgsConstructor
public class BookHomeDTO {
    private List<Book> banners;
    private List<Book> recentBooks;
    private List<Book> hotBooks;
}
