package com.nowcoder.community.Controller;

import com.nowcoder.community.entity.Page;
import org.springframework.ui.Model;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    // 注入组件
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    // 定义访问路径
    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前springMVC会自动实例化Model和Page，并将page注入model
        //所以在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        // 查询 - 前十条数据 - 主页 所以user id = 0 ->  list是user id
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());

        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list!=null){
            for(DiscussPost post:list){
               Map<String,Object> map = new HashMap<>();
               map.put("post",post);
               User user = userService.findUserById(post.getUserid());
               map.put("user",user);
               discussPosts.add(map);
            }
        }
        //给结果放到model里
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }// 返回模版路径

}
