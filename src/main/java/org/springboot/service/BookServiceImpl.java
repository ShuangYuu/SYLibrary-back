package org.springboot.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springboot.bean.Book;
import org.springboot.bean.request.BookRequest;
import org.springboot.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void addBook(Book book) {
        bookMapper.addBook(book);
    }

    @Override
    public void deleteBook(Integer id) {
        bookMapper.deleteBookById(id);
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
}
