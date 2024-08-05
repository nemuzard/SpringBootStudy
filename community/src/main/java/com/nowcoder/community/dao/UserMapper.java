package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Mapper
@SpringBootApplication

public interface UserMapper {
    //需要给每一个办法一个配置文件
    //查询
    // 根据用户名查id
    User selectById(int id);
    //根据用户名
    User selectByName(String username);
    User selectByEmail(String email);

    // 增加
    int insertUser(User user);
    //修改用户
    int updateStatus(int id,int status);

    int updateHeader(int id,String headerUrl);
    int updatePassword(int id, String password);

}
