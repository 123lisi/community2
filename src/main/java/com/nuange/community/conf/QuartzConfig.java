package com.nuange.community.conf;

import com.nuange.community.quartz.AlphaJob;
import com.nuange.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置 ->数据库->调用
@Configuration
public class QuartzConfig {

    //FactoryBean可简化Bean的实例化过程；
    //1.通过FactoryBean封装了Bean的实例化过程
    //2.将FactoryBean装配到spring容器中
    //3.将FactoryBean注入给其他的Bean
    //4.该Bean得到的是FactoryBean所管理的对象实例化

    //配饰JobDetail
//    @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        //设置工作的类
        factoryBean.setJobClass(AlphaJob.class);
        //设置名字
        factoryBean.setName("alphaJob");
        //设置组名
        factoryBean.setGroup("alphaJobGrop");
        //设置是否一直存在
        factoryBean.setDurability(true);
        //任务是否可以恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //配置Trigger
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        //设置Jobdetail
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        //设置隔多少毫秒执行一次
        factoryBean.setRepeatInterval(3000);
        //设置存储状态的对象
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    //刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        //设置工作的类
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        //设置名字
        factoryBean.setName("postScoreRefreshJob");
        //设置组名
        factoryBean.setGroup("communityJobGroup");
        //设置是否一直存在
        factoryBean.setDurability(true);

        return factoryBean;
    }

    //配置Trigger
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        //设置Jobdetail
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName(" postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        //设置隔多少毫秒执行一次
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        //设置存储状态的对象
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}

