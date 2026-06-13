package org.springboot.service;

import org.springboot.entity.Book;
import org.springboot.entity.dto.BookHomeDTO;
import org.springboot.entity.request.BookRequest;

import java.util.List;

public interface BookService {

    Object getBooks(BookRequest bookRequest);

    void addBook(Book book);

    void deleteBook(String bookId);

    void updateBook(Book book);

    List<Book> getSwiperBooks();

    List<Book> getNewBooks();

    BookHomeDTO getHomeBooks();
}
