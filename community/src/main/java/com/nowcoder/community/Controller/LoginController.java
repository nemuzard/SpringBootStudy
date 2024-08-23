package com.nowcoder.community.Controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

@Controller
public class LoginController implements CommunityConstant {
    // 创建logger对象，下面的try catch需要logger
    private static final Logger logger   = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    //直接增加一个方法，处理访问注册页面的【请求】
    @RequestMapping(path="/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register"; //返回模板的路径
    }
    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login"; //返回登录的路径
    }
    //定义一个方法处理请求 -- Spring MVC 自动给User注入到Model里面

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(path="/register",method=RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        // 说明注册成功，然后跳到首页--不跳登录因为它还需要激活（其实无所谓）
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功，激活邮件已发送，请激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            //失败 给下面这发送给注册页面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }

    }
    //http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userid}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userid") int userId, @PathVariable("code") String code){
        int result = userService.activationStatus(userId,code);
        // 成功就跳登录页面
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","Activation Success!");
            model.addAttribute("target","/login"); // jump to login page
        // 失败了去首页
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","FAILED: Activation Repeated");
            model.addAttribute("target","/index");

        }else{
            model.addAttribute("msg","Activation Failed, wrong activation code");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }


    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    // 是一个特殊的东西 图片 所以要void 我们要手动输出
    // 敏感数据 用session
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text); // text 传入 然后根据text生成图片
        // 将验证码 存入session
        session.setAttribute("kaptcha",text);
        // 图片直接输出给浏览器
        // 首先声明访问的类型
        response.setContentType("image/png");
        // 输出流
        try {
            // 这个流可以不用关闭 springMVC维护的会自动关
            OutputStream os = response.getOutputStream();
            // 输出的图片,png格式，用os输出
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败:"+e.getMessage());
        }
    }
    /***
     * 可以一样的路径 但是请求方式要有区别
     */
    @RequestMapping(path="/login",method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response){
        // 首先判断验证码 - 不对直接返回
        String kaptcha = (String)session.getAttribute("kaptcha");
        // 1. kaptcha是空的，2.输入的验证码 是空的 3.验证码不正确
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)|| !kaptcha.equalsIgnoreCase(code)){
            //给提示放入model里面返回给页面
            model.addAttribute("codeMsg","verification code is incorrect");
            return "/site/login";
        }

        // 检查账号，密码： 可以交给业务层去处理
        int expiredSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            // 给客户端传送一个cookie
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            // 设置cookie有效时间
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }else {

            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }

    }
    /***
     * 处理logout页面的请求 不需要处理什么数据所以简单的get就可以
     * 现在浏览器里面已经有cookie了 需要传给客户端 通过@CookieValue -- 因为我们之前login了
     */
    @RequestMapping(path = "/logout",method=RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login" ;// 重定向的时候默认get请求
    }
}
