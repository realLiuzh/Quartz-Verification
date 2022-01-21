package com.lzh.test;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class _02_QuartzTestInteresting {

    public static void main(String[] args) {

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            //定义一个job并且把它绑在HelloJob上
            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            //Trigger 触发这个job立刻执行，并且每40s执行一次
            SimpleTrigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1)
                            .repeatForever())
                    .build();


            //告诉schedule执行job使用trigger
            scheduler.scheduleJob(job,trigger);

            Thread.sleep(100000);

            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
