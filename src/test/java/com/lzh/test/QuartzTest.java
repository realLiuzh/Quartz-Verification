package com.lzh.test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.concurrent.TimeUnit;

public class QuartzTest {

    public static void main(String[] args) {

        Scheduler scheduler = null;
        try {
            //1.创建调度器
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            //2.创建任务
            //HelloJob中实现了Job接口
            JobDetail job = JobBuilder.newJob(HelloJob.class)
                    .withIdentity("job1", "group1") //对Job进行命名和分组
                    .build();

            //3.创建触发器
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1") //对trigger进行分组和命名
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(5)   //执行频率
                            .repeatForever())           //重复频率
                    .build();

            scheduler.scheduleJob(job, trigger);

            TimeUnit.SECONDS.sleep(20);
            scheduler.shutdown();


        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }


    }


}
