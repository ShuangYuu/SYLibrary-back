package org.springboot.service;

import org.springboot.entity.Book;

import java.util.List;

public interface UserFavoriteBookService {

    List<Book> getFavorites(Integer userId);

    boolean isFavorite(Integer userId, Integer bookId);

    void addFavorite(Integer userId, Integer bookId);

    void deleteFavorite(Integer userId, Integer bookId);
}
