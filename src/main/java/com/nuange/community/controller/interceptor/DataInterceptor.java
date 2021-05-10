package com.nuange.community.controller.interceptor;

import com.nuange.community.entity.User;
import com.nuange.community.service.DataService;
import com.nuange.community.unity.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private DataService dateService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计uv
        //获取访问的ip
        String ip = request.getRemoteHost();
        dateService.recordUV(ip);
        //统计DAU
        //获取到user
        User user = hostHolder.getUser();
        if (user != null){
            dateService.recordDAU(user.getId());
        }
        return true;
    }
}
