package org.springboot.mapper;

import org.apache.ibatis.annotations.*;
import org.springboot.bean.RefreshToken;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    @Select("select * from refreshToken " +
            "where jti = #{ jti } AND id = #{ id }")
    Optional<RefreshToken> findByJtiAndId(RefreshToken refreshToken);

    @Delete("delete from refreshToken " +
            "where id = #{ id }")
    void deleteRefreshToken(RefreshToken refreshToken);

    @Insert("insert into refreshToken(id, type, jti, role) " +
            "values(#{ id }, #{ type }, #{ jti }, #{ role })")
    void addRefreshToken(RefreshToken refreshToken);

}
