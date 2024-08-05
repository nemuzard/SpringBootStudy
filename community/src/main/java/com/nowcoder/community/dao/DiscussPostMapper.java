package com.nowcoder.community.dao;


import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Mapper
@SpringBootApplication
public interface DiscussPostMapper {
    //分页查询的功能，返回的是集合 -很多贴子 userId 是因为未来需要查看自己的帖子的功能
    // 0 代表不是用户
    // offset (start,end)
    List<DiscussPost> selectDiscussPosts(int userId,int offset, int limit);

    // 返回表里的数据 一共多少
    int selectDiscussPostRows(@Param("userId") int userid);

}
