package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.bean.User;
import org.springboot.bean.dto.JwtUser;
import org.springboot.bean.request.UserLoginRequest;
import org.springboot.bean.request.UserRequest;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    List<User> allUsers();

    @Select({
            "<script>",
            "SELECT * FROM user",
            "<where>",
            "  <if test='req.username != null and req.username != \"\"'>",
            "    username LIKE CONCAT('%', #{ req.username }, '%')",
            "  </if>",
            "</where>",
//            "order by id desc",
            "</script>"
    })
    List<User> getUsers(@Param("req") UserRequest userRequest);

    @Insert("insert into user(username, cardID, age, sex, phone, address) " +
            "values( #{ username }, #{ cardID }, #{ age }, #{ sex }, #{ phone }, #{ address })")
    int addUser(User user);

    @Update("update user " +
            "set username = #{ username }, age = #{ age }, sex = #{ sex }, phone = #{ phone }, password = #{ password }, address = #{ address } " +
            "where id = #{ id }")
    int updateUser(User user);

    @Delete("delete from user " +
            "where id = #{ id }")
    int deleteUser(Integer id);

    @Select("select * from user " +
            "where phone = #{ phone }")
    User login(UserLoginRequest userLoginRequest);

    @Select("select * from user " +
            "where id = #{ id }")
    JwtUser searchById(JwtUser jwtUser);
}
