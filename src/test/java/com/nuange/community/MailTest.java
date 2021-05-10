package com.nuange.community;

import com.nuange.community.unity.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine; //注入模板引擎
    @Test
    public void testTestMail(){
        mailClient.sendMail("361789224@qq.com","Test","Welcome..");
    }
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","zhangsan");
        String content = templateEngine.process("/demo/testSendMail.html", context);
        System.out.println(content);
        mailClient.sendMail("361789224@qq.com","您好呀！",content);
    }
}
