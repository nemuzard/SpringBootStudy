package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    //domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
    // 返回结果封装到map
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if (user==null){
            throw new IllegalArgumentException("cannot be null");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","username cannot be null");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","Password cannot be null");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","Email cannot be null");
            return map;
        }
        // 验证账号是否用
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","Username has been used");
            return map;
        }
        //
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","Email has been used");
            return map;
        }
        //------------用户信息 ---------------
        // 注册 加密0-5位数
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        // 密码的形式是 原密码+加密的salt
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        // 用户类型和状态
        user.setType(0);
        user.setStatus(0);
        // 激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 初始头像 牛客网的头像是存在下面的网站里面，然后一共有1000多, %d 是个占位符，然后后面生产一个1000以内的随机数字
        user.setHeaderUrl(String.format("http://images.nowcoder.com/header/%dt.png",new Random().nextInt(1000)));
        // 注册时间 = 当前时间
        user.setCreateTime(new Date());

        // 给上面的信息 添加到我们的user库里面
        userMapper.insertUser(user);

        // 给用户发送激活邮件，需要发送html的 因为有链接
        //利用 mail下面的activation发邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // 希望服务器用什么路径处理请求?
        // http://localhost:8080/community/activation/101/code 用户id+激活码
        String url = domain+contextPath+ "/activation"+ "/" +user.getId()+ "/" + user.getActivationCode();
        context.setVariable(url,url);

        // 利用模板引擎生邮件内容 + 发送邮件
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    public int activationStatus(int userId,String code){
        // 首先查找用户，在看看激活码对不对
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    /***
     *
     * @param password: 需要加密，因为我们存在库里的密码是加密的，我们不能直接对比
     * @param expiredSeconds: 多少秒之后过期
     */
    public  Map<String,Object> login(String username, String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能是空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能是空");
            return map;
        }
        // 验证账号合法性
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        // 验证账号状态 -- 是否激活
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        // 验证密码
        //首先对传入的密码加密
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //如果成功 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        //随机生成ticket
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        //设置完之后要调用mapper给这些存进去
        loginTicketMapper.insertLoginTicket(loginTicket);
        // 存到map里 返回给客户端-浏览器只需要key
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /***
     *
     * @param ticket
     */
    public void logout(String ticket){
        //update ticket status to 1 which is invalid
        loginTicketMapper.updateStatus(ticket,1);

    }

}
