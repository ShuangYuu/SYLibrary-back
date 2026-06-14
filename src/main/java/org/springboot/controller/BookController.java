package org.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springboot.common.Result;
import org.springboot.entity.Book;
import org.springboot.entity.request.BookImportSelectedRequest;
import org.springboot.entity.request.BookRequest;
import org.springboot.entity.request.ExternalBookSearchRequest;
import org.springboot.service.impl.BookImportServiceImpl;
import org.springboot.service.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookServiceImpl bookServiceImpl;

    @Autowired
    private BookImportServiceImpl bookImportServiceImpl;

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

    @DeleteMapping("/{bookId}")
    public Result deleteBook(@PathVariable String bookId) {
        bookServiceImpl.deleteBook(bookId);
        return Result.success();
    }

    @GetMapping("/home")
    public Result getHomeBooks() {
        return Result.success(bookServiceImpl.getHomeBooks());
    }

    @GetMapping("/external/search")
    public Result searchExternalBooks(ExternalBookSearchRequest request) {
        return Result.success(bookImportServiceImpl.searchExternalBooks(request));
    }

    @PostMapping("/import/selected")
    public Result importSelectedBooks(@RequestBody BookImportSelectedRequest request) {
        return Result.success(bookImportServiceImpl.importSelectedBooks(request));
    }

    @PostMapping("/import/open-library")
    public Result importOpenLibraryBooks(@RequestBody ExternalBookSearchRequest request) {
        return Result.success(bookImportServiceImpl.importOpenLibraryBooks(request));
    }
}
