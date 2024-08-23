package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    /***
     * 我们的表都是以 Ticket为中心的，不管是查询还是登录
     * @param loginTicket
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "Values(#{userId},#{ticket},#{status},#{expired}) "
    })
    // 自动生成id
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);
    @Select({
            "select id, user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);
    // 更改ticket状态，因为需要退出登录 -- 在互联网里很少真的删除数据
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
