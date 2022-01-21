package com.lzh.test;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.concurrent.TimeUnit;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author 志昊的刘
 * @date 2022/1/20
 */
public class _03_UsingQuartz {

    public static void main(String[] args) {

        try {
            //创建一个调度程序
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            //创建一个job 并把HelloJob绑定上去
            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("myjob", "group1")
                    .build();

            //Trigger触发job立刻运行，1s钟1次
            Trigger trigger = newTrigger()
                    .withIdentity("myTrigger", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1)
                            .repeatForever()).build();

            //告诉quartz去调度job时使用trigger
            scheduler.scheduleJob(job,trigger);

            TimeUnit.SECONDS.sleep(4);
            scheduler.shutdown();


        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
