package com.lzh.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

//@Configuration
public class QuartzConfig {

    //这些bean会自动关联到scheduler上

    @Bean
    public JobDetail jobDetail() {
        return newJob()
                .withIdentity("start_of_day", "start_of_day")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return newTrigger()
                .forJob(jobDetail())
                //JobDetail和Trigger的唯一标识可以相同，因为他们是不同的类。
                .withIdentity("start_of_day", "start_of_day")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }


}
