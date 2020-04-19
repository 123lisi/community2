package com.nuange.community.service;

import com.nuange.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void  init(){
        System.out.println("初始化");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }
    public String getDao(){
        return alphaDao.select();
    }
}
