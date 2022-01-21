package com.lzh.test;


import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class _01_QuartzTest {

    public static void main(String[] args) {

        try {
            Scheduler scheduler= StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}
