package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.entity.Book;

import java.util.List;

@Mapper
public interface UserFavoriteBookMapper {

    @Select("select count(1) from user_favorite_book where user_id = #{ userId } and book_id = #{ bookId }")
    int countByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

    @Insert("insert into user_favorite_book(user_id, book_id) values(#{ userId }, #{ bookId })")
    void addFavorite(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

    @Delete("delete from user_favorite_book where user_id = #{ userId } and book_id = #{ bookId }")
    void deleteFavorite(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

    @Select("select b.book_id, b.name, b.author, b.imageUrl, b.tags, b.comment, " +
            "b.source_platform as sourcePlatform, b.source_id as sourceId, b.isbn, b.publisher, " +
            "b.publish_year as publishYear, b.description, b.`createdTime` " +
            "from user_favorite_book f " +
            "join book b on f.book_id = b.book_id " +
            "where f.user_id = #{ userId } " +
            "order by f.createdTime desc, f.id desc")
    List<Book> getFavoritesByUserId(@Param("userId") Integer userId);
}
