# Quartz

官网地址：http://www.quartz-scheduler.org/

CSDN地址：[https://blog.csdn.net/weixin_45921593/article/details/122618313](https://blog.csdn.net/weixin_45921593/article/details/122618313)

## 前置概念

定时任务是指 **在指定的时间，做指定的动作。**

我们为什么需要定时任务呢？考虑以下需求：

| 需求                                                         | 需求特点                  |
| ------------------------------------------------------------ | ------------------------- |
| 某电商平台做促销，当到达促销时间，修改商品价格。             | 一次性                    |
| B站up🐖每天中午12点可以看到之前24小时内，自己作品的播放量、评论数、三连等相关信息。 | 每天循环                  |
| 你如果有信用卡，每笔消费的30天后，会提醒你还款。             | 动态一次性                |
| 每周五提醒员工写周报                                         | 每周循环 星期确定         |
| 每个月最后一天提醒你写月报                                   | 每月循环 日期不确定       |
| 每年母亲节（5月的第二个周日），给母亲发送祝福                | 每年按规则循环 日期不确定 |
| 每隔5个月奖励自己去吃大餐                                    | 每5个月循环               |

## 快速入门

**下载安装**

使用maven地址。

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
            <version>2.6.2</version>
        </dependency>
```

**配置**

Quartz使用 **quartz.properties**配置文件。一开始这并不是必须的，但是如果使用最基本的配置之外的任何东西，必须将其配置在类路径下。

> Quartz is a very configurable application.The best way to configure Quartz is to edit a quartz.properties file, and place it in your application’s classpath.

quartz.properties

```properties
#This scheduler’s name will be "MyScheduler".
org.quartz.scheduler.instanceName=MyScheduler

#There are 3 threads in the thread pool, which means that a maximum of 3 jobs can be run simultaneously.
org.quartz.threadPool.threadCount=3

#All of Quartz’s data, such as details of jobs and triggers, is held in memory (rather than in a database). Even if you have a database and want to use it with Quartz, I suggest you get Quartz working with the RamJobStore before you open up a whole new dimension by working with a database.
org.quartz.jobStore.class=org.quartz.simpl.RAMJobStore
```

**开始一个简单程序**

Quartz中的三大组件：

![image-20220121113058163](https://gitee.com/lzhnynl/picture/raw/master/image-20220121113058163.png)

HelloJob.java

```java
//实现Job接口
public class HelloJob implements Job {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("HelloJob.execute" + "  " + simpleDateFormat.format(new Date()) + "  " + Thread.currentThread().getName());
    }
}
```



_02_QuartzTestInteresting.java

```java
public class _02_QuartzTestInteresting {

    public static void main(String[] args) {

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            //定义一个job并且把它绑在HelloJob上
            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            //Trigger 触发这个job立刻执行，并且每1s执行一次
            SimpleTrigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()		//with Schedule 安排计划
                            .withIntervalInSeconds(1)
                            .repeatForever())
                    .build();


            //告诉schedule执行job使用trigger
            scheduler.scheduleJob(job,trigger);		   //Scheduler 	调度程序

            Thread.sleep(100000);

            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}

```

## Tutorials

### Lesson1:Using Quartz

使用Scheduler前首先使用SchedulerFactory将其实例化。

>  Note that once a scheduler is shutdown, it cannot be restarted without being re-instantiated. Triggers do not fire (jobs do not execute) until the scheduler has been started, nor while it is in the paused state.

_03_UsingQuartz.java

```java
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
```



### Lesson2:The Quartz API,Jobs And Triggers

>  Once created the Scheduler interface can be used add, remove, and list Jobs and Triggers, and perform other scheduling-related operations (such as pausing a trigger).



> When the Job’s trigger fires (more on that in a moment), the execute(..) method is invoked by one of the scheduler’s worker threads. The *JobExecutionContext* object that is passed to this method provides the job instance with information about its “run-time” environment - a handle to the Scheduler that executed it, a handle to the Trigger that triggered the execution, the job’s JobDetail object, and a few other items.
>
>  传递给此方法的 JobExecutionContext 对象为作业实例提供有关其“运行时”环境的信息——执行它的调度程序的句柄、触发执行的触发器的句柄、作业的 JobDetail 对象和 其他几项。





### Lesson 9:Job Stroes

> JobStore’s are responsible for keeping track of all the “work data” that you give to the scheduler: jobs, triggers, calendars, etc.

Job Stores负责保存所有的给到调度程序的"工作数据"的踪迹，像：jobs、triggers、calendars等等。

> You declare which JobStore your scheduler should use (and it’s configuration settings) in the properties file (or object) that you provide to the SchedulerFactory that you use to produce your scheduler instance.

你要在peoperties配置文件中声明你的scheduler使用哪种JobStore，由此提供给SchedulerFactory去生成scheduler实例。

#### RAMJobStore

- 它是最简单的(易于配置) 效率最高的JobStore；
- 将所有数据保存在内存；
- 缺点是不具有非易失性。你的程序停止时，所有的调度数据全部丢失；
- 该缺点对于一些应用是可以接受的，但是对于其它的程序，这可能是灾难性的；

Configuring Quartz to use RAMJobStore

```properties
org.quartz.jobStores.class = org.quartz.simple.RAMJobStore
```

仅此而已。

#### JDBCJobStore

- 所有数据存放在数据库(相对于RAMJobStore来说更难配置)；
- 相对于RAMJobStore来说它并不快，但是如果在表上创建索引，它其实并不慢；
- 

**How to use**

1. You must first create a set of database tables for Quartz to use(table-creation SQL scripts in the end & all the tables start with the prefix "QRTZ_");
2. You need to decide what type of transactions you application needs.(决定您的应用程序需要什么类型的事务)
   -  If you don’t need to tie your scheduling commands (such as adding and removing triggers) to other transactions,  You can let Quartz manage the transaction by using **JobStoreTX** as your JobStore(this is the most common selection) ;
   - If you need Quartz to work along with other transactions (i.e. within a J2EE application server), then you should use **JobStoreCMT** - in which case Quartz will let the app server container manage the transactions;
3. setting up a DataSource from which JDBCJobStore can get connections to your database;



**Configuring Quartz to use JobStoreTx** (管理事务)

```properties
org.quartz.jobstore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
```

**Configuring JDBCJobStore to use a DriverDelegate**(DriverDelegate负责做所有的JDBC操作)

```properties
org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
```

**Configuring JDBCJobStore with the Table Prefix**

```properties
org.quartz.jobStore.tablePrefix = QRTZ_
```

**Configuring JDBCJobStore with the name of the DataSource to use**

```properties
org.quartz.jobStore.dataSource = myDS
```

#### group and name

group是用来标记、方便管理的；name是用来标记的。

![image-20220121113338949](https://gitee.com/lzhnynl/picture/raw/master/image-20220121113338949.png)

需求：由于某些原因，需要修改昨天的物流信息，那就要使昨天的结算任务重新运行一次

解决：在管理后台提供一个**手动触发任务功能**

![image-20220121113823253](https://gitee.com/lzhnynl/picture/raw/master/image-20220121113823253.png)



## CornExpression

|            | 秒   | 分   | 时   | 日          | 月   | 星期           | 年        |
| ---------- | ---- | ---- | ---- | ----------- | ---- | -------------- | --------- |
| 允许的符号 | ,-*/ | ,-*/ | ,-*/ | ,-*/  ? L W | ,-*/ | ,-*/ ? L #     | ,-*/      |
| 允许的值   | 0-59 | 0-59 | 0-23 | 1-31        | 0-11 | 1-7（1为周日） | 1970-2199 |

![image-20220121114135125](https://gitee.com/lzhnynl/picture/raw/master/image-20220121114135125.png)



## SQL-Creation scripts

```sql
#
# In your Quartz properties file, you'll need to set
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
#
#
# By: Ron Cordell - roncordell
#  I didn't see this anywhere, so I thought I'd post it here. This is the script from Quartz to create the tables in a MySQL database, modified to use INNODB instead of MYISAM.

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS(
SCHED_NAME VARCHAR(120) NOT NULL,
JOB_NAME VARCHAR(190) NOT NULL,
JOB_GROUP VARCHAR(190) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
JOB_CLASS_NAME VARCHAR(250) NOT NULL,
IS_DURABLE VARCHAR(1) NOT NULL,
IS_NONCONCURRENT VARCHAR(1) NOT NULL,
IS_UPDATE_DATA VARCHAR(1) NOT NULL,
REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(190) NOT NULL,
TRIGGER_GROUP VARCHAR(190) NOT NULL,
JOB_NAME VARCHAR(190) NOT NULL,
JOB_GROUP VARCHAR(190) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
NEXT_FIRE_TIME BIGINT(13) NULL,
PREV_FIRE_TIME BIGINT(13) NULL,
PRIORITY INTEGER NULL,
TRIGGER_STATE VARCHAR(16) NOT NULL,
TRIGGER_TYPE VARCHAR(8) NOT NULL,
START_TIME BIGINT(13) NOT NULL,
END_TIME BIGINT(13) NULL,
CALENDAR_NAME VARCHAR(190) NULL,
MISFIRE_INSTR SMALLINT(2) NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(190) NOT NULL,
TRIGGER_GROUP VARCHAR(190) NOT NULL,
REPEAT_COUNT BIGINT(7) NOT NULL,
REPEAT_INTERVAL BIGINT(12) NOT NULL,
TIMES_TRIGGERED BIGINT(10) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_CRON_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(190) NOT NULL,
TRIGGER_GROUP VARCHAR(190) NOT NULL,
CRON_EXPRESSION VARCHAR(120) NOT NULL,
TIME_ZONE_ID VARCHAR(80),
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(190) NOT NULL,
TRIGGER_GROUP VARCHAR(190) NOT NULL,
BLOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_CALENDARS (
SCHED_NAME VARCHAR(120) NOT NULL,
CALENDAR_NAME VARCHAR(190) NOT NULL,
CALENDAR BLOB NOT NULL,
PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
ENGINE=InnoDB;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_GROUP VARCHAR(190) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_FIRED_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
ENTRY_ID VARCHAR(95) NOT NULL,
TRIGGER_NAME VARCHAR(190) NOT NULL,
TRIGGER_GROUP VARCHAR(190) NOT NULL,
INSTANCE_NAME VARCHAR(190) NOT NULL,
FIRED_TIME BIGINT(13) NOT NULL,
SCHED_TIME BIGINT(13) NOT NULL,
PRIORITY INTEGER NOT NULL,
STATE VARCHAR(16) NOT NULL,
JOB_NAME VARCHAR(190) NULL,
JOB_GROUP VARCHAR(190) NULL,
IS_NONCONCURRENT VARCHAR(1) NULL,
REQUESTS_RECOVERY VARCHAR(1) NULL,
PRIMARY KEY (SCHED_NAME,ENTRY_ID))
ENGINE=InnoDB;

CREATE TABLE QRTZ_SCHEDULER_STATE (
SCHED_NAME VARCHAR(120) NOT NULL,
INSTANCE_NAME VARCHAR(190) NOT NULL,
LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
CHECKIN_INTERVAL BIGINT(13) NOT NULL,
PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
ENGINE=InnoDB;

CREATE TABLE QRTZ_LOCKS (
SCHED_NAME VARCHAR(120) NOT NULL,
LOCK_NAME VARCHAR(40) NOT NULL,
PRIMARY KEY (SCHED_NAME,LOCK_NAME))
ENGINE=InnoDB;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);

commit;

```

















































