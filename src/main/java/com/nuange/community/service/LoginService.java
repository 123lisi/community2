package com.nuange.community.service;

import com.nuange.community.dao.LoginTicketMapper;
import com.nuange.community.dao.UserMapper;
import com.nuange.community.entity.LoginTicket;
import com.nuange.community.entity.User;
import com.nuange.community.unity.CommunityUnity;
import com.nuange.community.unity.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理登录逻辑的类
 */
@Service
public class LoginService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    /**
     * 登录逻辑的方法
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        //空值的处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        //验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUnity.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        //验证用户之前是否登录过
//        LoginTicket selectByUserId = loginTicketMapper.selectByUserId(user.getId());
//        if (selectByUserId == null) {
            //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUnity.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds*1000));
//            loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket", loginTicket.getTicket());
//        }
//        else if (selectByUserId.getStatus() != 1) {
//            loginTicketMapper.updateExpired(selectByUserId.getTicket(), new Date(System.currentTimeMillis() + expiredSeconds*1000 ));
//            map.put("ticket", selectByUserId.getTicket());
//        } else {
//            loginTicketMapper.updateExpired(selectByUserId.getTicket(), new Date(System.currentTimeMillis() + expiredSeconds*1000 ));
////            loginTicketMapper.updateStatus(selectByUserId.getTicket(), 0);
//            map.put("ticket", selectByUserId.getTicket());
//        }

        return map;
    }

    //退出登录的方法
    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    //查询凭证的方法
    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
//        return loginTicket;
    }
}
