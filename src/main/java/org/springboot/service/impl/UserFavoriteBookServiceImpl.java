package org.springboot.service.impl;

import org.springboot.entity.Book;
import org.springboot.exception.InvalidRequestException;
import org.springboot.mapper.UserFavoriteBookMapper;
import org.springboot.service.UserFavoriteBookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteBookServiceImpl implements UserFavoriteBookService {

    private final UserFavoriteBookMapper userFavoriteBookMapper;

    public UserFavoriteBookServiceImpl(UserFavoriteBookMapper userFavoriteBookMapper) {
        this.userFavoriteBookMapper = userFavoriteBookMapper;
    }

    @Override
    public List<Book> getFavorites(Integer userId) {
        return userFavoriteBookMapper.getFavoritesByUserId(userId);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer bookId) {
        validateBookId(bookId);
        return userFavoriteBookMapper.countByUserIdAndBookId(userId, bookId) > 0;
    }

    @Override
    public void addFavorite(Integer userId, Integer bookId) {
        validateBookId(bookId);
        if (userFavoriteBookMapper.countByUserIdAndBookId(userId, bookId) == 0) {
            userFavoriteBookMapper.addFavorite(userId, bookId);
        }
    }

    @Override
    public void deleteFavorite(Integer userId, Integer bookId) {
        validateBookId(bookId);
        userFavoriteBookMapper.deleteFavorite(userId, bookId);
    }

    private void validateBookId(Integer bookId) {
        if (bookId == null) {
            throw new InvalidRequestException("bookId不能为空");
        }
    }
}
