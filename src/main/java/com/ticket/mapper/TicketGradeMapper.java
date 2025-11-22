package com.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.model.entity.TicketGrade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketGradeMapper extends BaseMapper<TicketGrade> {
    int incrStockWithVersion(
            @Param("ticketGradeId") Long ticketGradeId,
            @Param("count") Integer count,
            @Param("version") Integer version
    );
}
