package com.ekertree.easypunch.utils;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.log.Log;
import com.ekertree.easypunch.pojo.Task;
import com.ekertree.easypunch.service.TaskService;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: CronUtils
 * Description:
 * date: 2022/8/6 15:05
 *
 * @author Ekertree
 * @since JDK 1.8
 */
@Slf4j
public class ScheduleUtils {

    public static Punch punch;

    static {
        punch = new Punch();
    }

    public static void init() {
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        for (Task task : TaskService.getTaskList()) {
            log.info("初始化定时任务【"+task.getTaskName()+"】=>["+task.getCron()+"]");
            if ("晨检打卡".equals(task.getTaskName())) {
                CronUtil.schedule(task.getTaskId(), task.getCron(),()->{
                    punch.doMorningPunch();
                });
            }else if ("午检打卡".equals(task.getTaskName())) {
                CronUtil.schedule(task.getTaskId(), task.getCron(),()->{
                    punch.doNoonPunch();
                });
            }else if ("更新sessionUrl".equals(task.getTaskName())) {
                CronUtil.schedule(task.getTaskId(), task.getCron(),()->{
                    punch.updatePunchUserListSessionUrl();
                });
            }else if ("更新打卡状态".equals(task.getTaskName())) {
                CronUtil.schedule(task.getTaskId(), task.getCron(),()->{
                    punch.updatePunchUserListStatus();
                });
            }
        }
        log.info("系统将会每分钟更新一次定时任务的执行时间......");
        CronUtil.schedule("0 0/1 * * * ? ", (cn.hutool.cron.task.Task) ()->{
            for (Task task : TaskService.getTaskList()) {
                CronUtil.updatePattern(task.getTaskId(),new CronPattern(task.getCron()));
            }
        });
    }

    public static void start() {
        init();
        CronUtil.start();
    }
}
