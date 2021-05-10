package com.nuange.community.controller;

import com.nuange.community.entity.DiscussPost;
import com.nuange.community.entity.Page;
import com.nuange.community.entity.User;
import com.nuange.community.service.DiscussPostService;
import com.nuange.community.service.LikeService;
import com.nuange.community.service.UserService;
import com.nuange.community.unity.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        int discussPostRows = discussPostService.findDiscussPostRows(0);
        page.setRows(discussPostRows);
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> discussPostList = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (discussPostList !=null){
            for (DiscussPost post:discussPostList){
                Map<String,Object> map = new HashMap<>();
                map.put("psot",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    //拒绝访问时提示的页面
    @RequestMapping(path = "/denied", method = {RequestMethod.GET})
    public String getDeniedPage() {
        return "/error/404";
    }
}
