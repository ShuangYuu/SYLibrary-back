package org.springboot.service;

import org.springboot.bean.Book;
import org.springboot.bean.request.BookRequest;

import java.util.List;

public interface BookService {

    Object getBooks(BookRequest bookRequest);

    void addBook(Book book);

    void deleteBook(Integer id);

    void updateBook(Book book);

    List<Book> getSwiperBooks();

    List<Book> getNewBooks();
}
