package org.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springboot.entity.Book;
import org.springboot.entity.dto.BookHomeDTO;
import org.springboot.entity.request.BookRequest;
import org.springboot.mapper.BookMapper;
import org.springboot.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public Object getBooks(BookRequest bookRequest) {
        PageHelper.startPage(bookRequest.getPageNum(), bookRequest.getPageSize());
        List<Book> books = bookMapper.getBooksByName(bookRequest);
        return new PageInfo<>(books);
    }

    @Override
    public void addBook(Book book) {
        bookMapper.addBook(book);
    }

    @Override
    public void deleteBook(String bookId) {
        bookMapper.deleteBookById(bookId);
    }

    @Override
    public void updateBook(Book book) {
        bookMapper.updateBook(book);
    }

    @Override
    public List<Book> getSwiperBooks() {
        return bookMapper.getSwiperBooks();
    }

    @Override
    public List<Book> getNewBooks() {
        return bookMapper.getNewBooks();
    }

    @Override
    public BookHomeDTO getHomeBooks() {
        return new BookHomeDTO(
                bookMapper.getHomeBannerBooks(5),
                bookMapper.getRecentBooks(8),
                Collections.emptyList()
        );
    }
}
