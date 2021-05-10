package com.nuange.community;

import com.nuange.community.unity.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    @Test
    public void testSensitive() {
        String test = "这里可以赌 博，可以嫖 ,娼，可以开票，哈哈哈！";
        String s = sensitiveFilter.filter(test);
        System.out.println(s);
    }

    @Test
    public void testDate() {

        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime().after(new Date()));
        System.out.println(df.format(new Date()));
    }
}
