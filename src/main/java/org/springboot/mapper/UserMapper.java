package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.entity.User;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.UserLoginDTO;
import org.springboot.entity.request.UserRequest;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    List<User> allUsers();

    @Select({
            "<script>",
            "SELECT id, username, phone, age, sex, address, cardId FROM user",
            "<where>",
            "  <if test='req.username != null and req.username != \"\"'>",
            "    username LIKE CONCAT('%', #{ req.username }, '%')",
            "  </if>",
            "</where>",
            "order by id desc",
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

    @Update("update user set userImage = #{ userImage } where id = #{ id }")
    int updateUserImage(@Param("id") Integer id, @Param("userImage") String userImage);

    @Delete("delete from user " +
            "where id = #{ id }")
    int deleteUser(Integer id);

    @Select("select * from user " +
            "where phone = #{ phone }")
    List<User> findByPhone(UserLoginDTO userLoginDTO);

    @Select("select * from user " +
            "where id = #{ id }")
    JwtUser searchById(JwtUser jwtUser);
}
