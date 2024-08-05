package com.nowcoder.community.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alpha") // 给这个类起名字
public class AlphaController {

    @RequestMapping("/hello") // 这样只知道要返回网页，但是我们要返回字符串 所以加上
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Bot";
    }
}
