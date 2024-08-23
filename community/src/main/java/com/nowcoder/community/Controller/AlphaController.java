package com.nowcoder.community.Controller;

import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alpha") // 给这个类起名字
public class AlphaController {

    @RequestMapping("/hello") // 这样只知道要返回网页，但是我们要返回字符串 所以加上
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Bot";
    }

    //cookie 实例
    // 首先接受浏览器请求
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    //展示 返回(字符串)不是页面
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //要把cookie响应的时候存到response里面
        //创建一个cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效的范围
        cookie.setPath("/community/alpha");
        //cookie的生存时间 in seconds
        cookie.setMaxAge(60*10);
        // 发送cookie，给cookie放在response里面
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code")String code){ // 在cookie中code里面取值
        System.out.println(code);
        return "get Cookie";
    }

    //session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    //spring 可以自动创建/注入session对象
    public String setSession(HttpSession session){
        //session 什么都可以存 因为在服务端
        // 在session里面存两条数据
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session ";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    //spring 可以自动创建session对象
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id")); //1
        System.out.println(session.getAttribute("name")); // Test
        return "set session";
    }
}

