package org.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springboot.bean.Book;
import org.springboot.bean.request.BookRequest;
import org.springboot.common.Result;
import org.springboot.service.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookServiceImpl bookServiceImpl;

    @GetMapping("/page")
    public Result getBooks(BookRequest bookRequest) {
        return Result.success(bookServiceImpl.getBooks(bookRequest));
    }

    @PostMapping("/")
    public Result addBook(@RequestBody Book book) {
        bookServiceImpl.addBook(book);
        return Result.success();
    }

    @PutMapping("/")
    public Result updateBook(@RequestBody Book book) {
        bookServiceImpl.updateBook(book);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteBook(@PathVariable Integer id) {
        bookServiceImpl.deleteBook(id);
        return Result.success();
    }

    @GetMapping("/swiper")
    public Result getSwiperBooks() {
        return Result.success(bookServiceImpl.getSwiperBooks());
    }

    @GetMapping("/newBooks")
    public Result getNewBooks() {
        return Result.success(bookServiceImpl.getNewBooks());
    }
}
