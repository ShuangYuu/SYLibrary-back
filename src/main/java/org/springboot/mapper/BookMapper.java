package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.entity.Book;
import org.springboot.entity.request.BookRequest;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select({
            "<script>",
            "select book_id, name, author, imageUrl, tags, comment,",
            "source_platform as sourcePlatform, source_id as sourceId, isbn, publisher,",
            "publish_year as publishYear, description, `createdTime` from book",
            "<where>",
            "  <if test='name != null and name != \"\"'>",
            "    name LIKE CONCAT('%', #{name}, '%')",
            "  </if>",
            "  <if test='author != null and author != \"\"'>",
            "    AND author LIKE CONCAT('%', #{author}, '%')",
            "  </if>",
            "  <if test='tags != null and tags != \"\"'>",
            "    AND tags LIKE CONCAT('%', #{tags}, '%')",
            "  </if>",
            "</where>",
            "order by book_id desc",
            "</script>"
    })
    List<Book> getBooksByName(BookRequest bookRequest);

    @Insert("insert into book(name, author, imageUrl, tags, comment, source_platform, source_id, isbn, publisher, publish_year, description) " +
            "values(#{ name }, #{ author }, #{ imageUrl }, #{ tags }, #{ comment }, #{ sourcePlatform }, #{ sourceId }, #{ isbn }, #{ publisher }, #{ publishYear }, #{ description })")
    void addBook(Book book);

    @Update("update book " +
            "set name = #{ name }, author = #{ author }, imageUrl = #{ imageUrl }, tags = #{ tags }, comment = #{ comment } " +
            "where book_id = #{ book_id }")
    void updateBook(Book book);

    @Delete("delete from book where book_id = #{ bookId }")
    void deleteBookById(@Param("bookId") String bookId);

    @Select("select b.book_id, b.name, b.author, b.imageUrl, b.tags, b.comment, " +
            "b.source_platform as sourcePlatform, b.source_id as sourceId, b.isbn, b.publisher, " +
            "b.publish_year as publishYear, b.description, b.`createdTime` " +
            "from bannerbook ba " +
            "join book b on ba.book_id = b.book_id")
    List<Book> getSwiperBooks();

    @Select("select b.book_id, b.name, b.author, b.imageUrl, b.tags, b.comment, " +
            "b.source_platform as sourcePlatform, b.source_id as sourceId, b.isbn, b.publisher, " +
            "b.publish_year as publishYear, b.description, b.`createdTime` " +
            "from newbook n " +
            "join book b on n.book_id = b.book_id")
    List<Book> getNewBooks();

    @Select("select book_id, name, author, imageUrl, tags, comment, " +
            "source_platform as sourcePlatform, source_id as sourceId, isbn, publisher, " +
            "publish_year as publishYear, description, `createdTime` " +
            "from book " +
            "where imageUrl is not null and imageUrl <> '' " +
            "order by `createdTime` desc, book_id desc " +
            "limit #{ limit }")
    List<Book> getHomeBannerBooks(@Param("limit") int limit);

    @Select("select book_id, name, author, imageUrl, tags, comment, " +
            "source_platform as sourcePlatform, source_id as sourceId, isbn, publisher, " +
            "publish_year as publishYear, description, `createdTime` " +
            "from book " +
            "order by `createdTime` desc, book_id desc " +
            "limit #{ limit }")
    List<Book> getRecentBooks(@Param("limit") int limit);

    @Select("select count(1) from book where source_platform = #{ sourcePlatform } and source_id = #{ sourceId }")
    int countBySource(@Param("sourcePlatform") String sourcePlatform, @Param("sourceId") String sourceId);

    @Select("select count(1) from book where isbn = #{ isbn }")
    int countByIsbn(@Param("isbn") String isbn);

    @Select("select count(1) from book where name = #{ name } and author = #{ author }")
    int countByNameAndAuthor(@Param("name") String name, @Param("author") String author);
}
