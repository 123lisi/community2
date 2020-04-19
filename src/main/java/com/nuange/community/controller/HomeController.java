package com.nuange.community.controller;

import com.nuange.community.entity.DiscussPost;
import com.nuange.community.entity.Page;
import com.nuange.community.entity.User;
import com.nuange.community.service.DiscussPostService;
import com.nuange.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        int discussPostRows = discussPostService.findDiscussPostRows(0);
        page.setRows(discussPostRows);
        page.setPath("/index");
        List<DiscussPost> discussPostList = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (discussPostList !=null){
            for (DiscussPost post:discussPostList){
                Map<String,Object> map = new HashMap<>();
                map.put("psot",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "index";
    }
}
