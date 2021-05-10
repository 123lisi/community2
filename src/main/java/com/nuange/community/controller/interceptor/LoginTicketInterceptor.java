package com.nuange.community.controller.interceptor;

import com.nuange.community.entity.LoginTicket;
import com.nuange.community.entity.User;
import com.nuange.community.service.LoginService;
import com.nuange.community.service.UserService;
import com.nuange.community.unity.CookieUtil;
import com.nuange.community.unity.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 登录拦截器
 */


@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //在controller执行之前判断cookie中是否有user凭证
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            //查询凭证
            LoginTicket loginTicket = loginService.findLoginTicket(ticket);
            //检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户
                hostHolder.setUser(user);
                //构建用户认证的结果,并存入SecurityContext，以便于Security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,user.getPassword(),userService.getAuthorities(user.getId()));
                //存入security
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

            }
        }
        return true;
    }

    //在模板执行之前向模板中添加user对象
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
//            System.out.println("在模板中设置了:" + user.toString());
            modelAndView.addObject("loginUser", user);
        }
    }

    //在最后执行的时候清除掉user对象
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        //清理掉security
        SecurityContextHolder.clearContext();
    }
}
