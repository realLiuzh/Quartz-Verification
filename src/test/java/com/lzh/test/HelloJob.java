package com.lzh.test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

//实现Job接口
public class HelloJob implements Job {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("HelloJob.execute" + "  " + simpleDateFormat.format(new Date()) + "  " + Thread.currentThread().getName());
    }
}
