package com.nuange.community.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WkConfig {
    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);
    //获取配置文件的值
    @Value("${wk.image.storage}")
    private String wkImageStorage;
    //初始化的方法，只执行一次
    @PostConstruct
    public void init(){
        File file = new File(wkImageStorage);
        if (!file.exists()){
            file.mkdir();
            logger.info("创建WK图片目录："+wkImageStorage);
        }
    }
}
