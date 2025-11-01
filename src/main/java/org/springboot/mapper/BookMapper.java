package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.bean.Book;
import org.springboot.bean.request.BookRequest;

import java.util.List;

@Mapper
public interface BookMapper {

    // 管理端
    @Select("select * from book " +
            "where name LIKE CONCAT('%', #{ name }, '%') ")
    List<Book> getBooksByName(BookRequest bookRequest);

    @Insert("insert into book(name, author, isbn, cover, type, comment) " +
            "values(#{ name }, #{ author }, #{ isbn }, #{ cover }, #{ type }, #{ comment })")
    void addBook(Book book);

    @Update("update book " +
            "set name = #{ name }, author = #{ author }, isbn = #{ isbn }, cover = #{ cover }, type = #{ type }, comment = #{ comment } " +
            "where id = #{ id }")
    void updateBook(Book book);

    @Delete("delete from book where id = #{ id }")
    void deleteBookById (Integer id);

    //客户端
    @Select("select b.book_id, b.name, b.author, b.imageUrl, b.tags, b.comment " +
            "from bannerbook ba " +
            "join book b on ba.book_id = b.book_id")
    List<Book> getSwiperBooks();

    @Select("select b.book_id, b.name, b.author, b.imageUrl, b.tags, b.comment " +
            "from newbook n " +
            "join book b on n.book_id = b.book_id")
    List<Book> getNewBooks();
}
