package com.nuange.community.unity;

import com.nuange.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();
    //设置对象
    public void  setUser(User user){
        users.set(user);
    }
    //获取对象
    public User getUser(){
        return users.get();
    }
    //删除对象的方法
    public void clear(){
        users.remove();
    }

}
