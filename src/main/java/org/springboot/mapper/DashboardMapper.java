package org.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springboot.entity.dto.TrendPointDTO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DashboardMapper {

    @Select("select date(`createdTime`) as date, count(1) as count " +
            "from book " +
            "where `createdTime` >= #{ startDate } and `createdTime` < date_add(#{ endDate }, interval 1 day) " +
            "group by date(`createdTime`) " +
            "order by date asc")
    List<TrendPointDTO> countBookAddsByDate(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Select("select date(`createdTime`) as date, count(1) as count " +
            "from user " +
            "where `createdTime` >= #{ startDate } and `createdTime` < date_add(#{ endDate }, interval 1 day) " +
            "group by date(`createdTime`) " +
            "order by date asc")
    List<TrendPointDTO> countUserAddsByDate(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Select("select count(1) from user where `createdTime` < #{ startDate }")
    long countUsersBefore(@Param("startDate") LocalDate startDate);
}
