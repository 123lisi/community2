package com.nuange.community.controller;

import com.nuange.community.annotation.LoginRequired;
import com.nuange.community.entity.User;
import com.nuange.community.service.FollowService;
import com.nuange.community.service.LikeService;
import com.nuange.community.service.UserService;
import com.nuange.community.unity.CommunityConstant;
import com.nuange.community.unity.CommunityUnity;
import com.nuange.community.unity.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 用户设置的请求
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSetting() {
        return "site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String uploadHeaderUrl(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || !suffix.equals("png") || !suffix.equals("jpg") || !suffix.equals("jpeg") || !suffix.equals("gif")) {
            model.addAttribute("error", "文件的格式不正确！");
            return "/site/setting";
        }
        //生成随机名字
        fileName = CommunityUnity.generateUUID() + suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //储存文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!", e);
        }
        //更新当前用户的头像路径(web访问的路径)
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                //获取响应的输出流
                OutputStream os = response.getOutputStream();
        ) {

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取图片失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码的方法
     *
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        if (oldPassword == null) {
            model.addAttribute("olderror", "请输入之前的密码");
            return "/site/setting";
        }
        if (newPassword == null) {
            model.addAttribute("newerror", "修改的密码不能为空");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        String oldPwd = CommunityUnity.md5(oldPassword + user.getSalt());
        String newPwd = CommunityUnity.md5(newPassword + user.getSalt());
        if (oldPwd.equals(user.getPassword())) {
            userService.updatePassword(user.getId(), newPwd);
        } else {
            model.addAttribute("oldPassword", oldPassword);
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("olderror", "您输入的原先密码不对");
            return "/site/setting";
        }
        return "redirect:/index";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user ==null){
            throw  new RuntimeException("用户不存在");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //查询粉丝量
        long followerCount = followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasForllowed = false;
        if (hostHolder.getUser() != null){
            hasForllowed = followService.hasFollowed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasForllowed",hasForllowed);
        return "/site/profile";
    }
}
