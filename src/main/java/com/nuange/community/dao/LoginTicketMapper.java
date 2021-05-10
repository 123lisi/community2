package com.nuange.community.dao;

import com.nuange.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Select({
            "select user_id,ticket,status,expired ",
            "from login_ticket where user_id=#{userId}"
    }
    )
    LoginTicket selectByUserId(int userId);

    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket !=null\">",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);

    @Update({
            "update login_ticket set expired=#{expired},status=0 where ticket=#{ticket} "
    })
    int updateExpired(String ticket, Date expired);


}
