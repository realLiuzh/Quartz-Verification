package com.lzh.config;

import com.lzh.job.TestTask1;
import com.lzh.job.TestTask2;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author 志昊的刘
 * @date 2022/1/21
 */

@Configuration
public class QuartzConfig2 {


    @Bean
    public JobDetail testTask1() {
        return newJob(TestTask1.class)
                .withIdentity("testTask1")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger testTrigger1() {
        return newTrigger()
                .forJob(testTask1())
                .withIdentity("testTrigger1")
                .startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(5).repeatForever())
                .build();
    }


    @Bean
    public JobDetail testTask2() {
        return newJob(TestTask2.class)
                .withIdentity("testTask2")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger testTrigger2() {
        return newTrigger()
                .forJob(testTask2())
                .withIdentity("testTrigger2")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
    }


}
