package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.bean.Admin;
import org.springboot.bean.dto.JwtUser;
import org.springboot.bean.request.AdminRequest;
import org.springboot.bean.request.AdminLoginRequest;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("select * from admin")
    List<Admin> allAdmins();

    @Select({
            "<script>",
            "SELECT id, username, phone, email FROM admin",
            "<where>",
            "  <if test='req.username != null and req.username != \"\"'>",
            "    username LIKE CONCAT('%', #{req.username}, '%')",
            "  </if>",
            "</where>",
//            "order by id desc",
            "</script>"
    })
    List<Admin> getAdmins(@Param("req") AdminRequest adminRequest);

    @Insert("insert into admin(username, password, phone, email) " +
            "values( #{ username }, #{ password }, #{ phone }, #{ email })")
    int addAdmin(Admin admin);

    @Update("update admin " +
            "set username = #{ username }, phone = #{ phone }, email = #{ email } " +
            "where id = #{ id }")
    int updateAdmin(Admin admin);

    @Delete("delete from admin " +
            "where id = #{ id }")
    int deleteAdmin(Integer id);

    @Select("select * from admin " +
            "where username = #{ username }")
    Admin login(AdminLoginRequest adminLoginRequest);

    @Select("select * from admin " +
            "where id = #{ id }")
    JwtUser searchById(JwtUser jwtUser);
}
