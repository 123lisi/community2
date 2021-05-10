package com.nuange.community.service;

import com.nuange.community.dao.UserMapper;
import com.nuange.community.entity.User;
import com.nuange.community.unity.CommunityConstant;
import com.nuange.community.unity.CommunityUnity;
import com.nuange.community.unity.MailClient;
import com.nuange.community.unity.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisTemplate redisTemplate;

    public User findUserById(int userId) {
        /**寻找自己发布信息的方法*/
        User user = getCache(userId);
        if (user == null) {
            user = initCache(userId);
        }
        return user;
    }

    /**
     * 注册方法
     *
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "用户名已经存在");
            return map;
        }
        User byEmail = userMapper.selectByEmail(user.getEmail());
        if (byEmail != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUnity.generateUUID().substring(0, 5));
        user.setPassword(CommunityUnity.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        //设置激活码
        user.setActivationCode(CommunityUnity.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String activateUrl = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("activateUrl", activateUrl);
        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 激活账号的方法
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCacha(userId);
            return ACTIVATION_SUCCESS;
        } else {
            System.out.println("激活码错误");
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 修改头像路径的方法
     */
    public int updateHeaderUrl(int userId, String headUrl) {

        int rows = userMapper.updateHeader(userId, headUrl);
        clearCacha(userId);
        return rows;
    }

    /**
     * 修改密码的方法
     */
    public int updatePassword(int id, String password) {
        int rows = userMapper.updatePassword(id, password);
        clearCacha(id);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    //1.优先从缓存中取值
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    //2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //3.数据更新的时候删除缓存
    private void clearCacha(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    //获得权限的方法
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
