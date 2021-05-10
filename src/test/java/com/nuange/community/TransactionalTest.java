package com.nuange.community;

import com.nuange.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionalTest {
    @Autowired
    private AlphaService alphaService;
    @Test
    public void TestTra(){
        Object o = alphaService.save();
        System.out.println(o);
    }
}

