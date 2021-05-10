package com.nuange.community;

import com.nuange.community.dao.DiscussPostMapper;
import com.nuange.community.dao.LoginTicketMapper;
import com.nuange.community.dao.MessageMapper;
import com.nuange.community.dao.UserMapper;
import com.nuange.community.entity.DiscussPost;
import com.nuange.community.entity.LoginTicket;
import com.nuange.community.entity.Message;
import com.nuange.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
    }
    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("3666@qq.com");
        user.setHeaderUrl("http://www.nowcode.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(0, 0, 20,0);

        for (DiscussPost post:discussPosts){
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(101);
        System.out.println(rows);
    }
    @Test
    public  void testLogin(){
        LoginTicket ticket = new LoginTicket();
        ticket.setTicket("666");
        ticket.setUserId(101);
        ticket.setStatus(0);
        ticket.setExpired(new Date(System.currentTimeMillis() + 1000*60));
        loginTicketMapper.insertLoginTicket(ticket);
    }
    @Test
    public void testSelect(){
        LoginTicket ticket = loginTicketMapper.selectByTicket("666");
        System.out.println(ticket.getId());
        System.out.println(ticket.getTicket());
        loginTicketMapper.updateStatus(ticket.getTicket(),1);
    }
    @Test
    public void testInsert(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(149);
        discussPost.setTitle("爱国");
        discussPost.setCommentCount(2);
        discussPost.setContent("dsandiqwidq");
        discussPost.setCreateTime(new Date(System.currentTimeMillis()));
        discussPost.setScore(1);
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPostMapper.insertDiscussPost(discussPost);
    }
    @Test
    public void TestMessageMapper(){
        List<Message> messageList = messageMapper.selectConversations(111, 0, 20);
        for (Message message:messageList){
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        List<Message> list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message:list){
            System.out.println(message);
        }
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);
    }
}
