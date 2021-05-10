package com.nuange.community.service;

import com.nuange.community.dao.AlphaDao;
import com.nuange.community.dao.DiscussPostMapper;
import com.nuange.community.dao.UserMapper;
import com.nuange.community.entity.DiscussPost;
import com.nuange.community.entity.User;
import com.nuange.community.unity.CommunityUnity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")
public class AlphaService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    public AlphaService(){
//        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void  init(){
//        System.out.println("初始化");
    }
    @PreDestroy
    public void destroy(){
//        System.out.println("销毁AlphaService");
    }
    public String getDao(){
        return alphaDao.select();
    }
    //REQUITRED：支持当前事务(外部事务),如果不存在则创建新事务
    //REQUITRES_NEW：创建一个新事务，并且暂停当前事务（外部事务）
    //NESTED：如果当前存在事务（外部事务），则嵌套在改事务中执行（独立的提交和回滚）
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save(){
        //新增用户
        User user = new User();
        user.setUsername("李四");
        user.setSalt(CommunityUnity.generateUUID().substring(0,5));
        user.setPassword(CommunityUnity.md5("123") + user.getSalt());
        user.setEmail("361789224@qq.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("这是一个新人");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);
        Integer.valueOf("asd");
        return "ok";
    }
    public String save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user = new User();
                user.setUsername("李四");
                user.setSalt(CommunityUnity.generateUUID().substring(0,5));
                user.setPassword(CommunityUnity.md5("123") + user.getSalt());
                user.setEmail("361789224@qq.com");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("hello");
                post.setContent("这是一个新人");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);
                Integer.valueOf("asd");
                return "ok";
            }
        });
    }
}
