package com.ekertree.easypunch.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ekertree.easypunch.mapper.TaskMapper;
import com.ekertree.easypunch.pojo.Task;
import com.ekertree.easypunch.pojo.TaskExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import java.util.List;

/**
 * ClassName: TaskService
 * Description:
 * date: 2022/8/6 12:28
 *
 * @author Ekertree
 * @since JDK 1.8
 */
@Slf4j
public class TaskService {

    public static List<Task> getTaskList() {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            return mapper.selectByExample(null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
        return null;
    }

    public static void updateTaskById(Task task) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            if (StrUtil.isEmpty(task.getTaskName())) {
                log.error("任务名不能为空！");
                return;
            }
            if (StrUtil.isEmpty(task.getCron())) {
                log.error("Cron表达式不能为空！");
                return;
            }
            mapper.updateByPrimaryKey(task);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public static void addTask(Task task) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            task.setTaskId(IdUtil.getSnowflakeNextIdStr());
            if (StrUtil.isEmpty(task.getTaskName())) {
                log.error("任务名不能为空！");
                return;
            }
            if (StrUtil.isEmpty(task.getCron())) {
                log.error("Cron表达式不能为空！");
                return;
            }
            mapper.insert(task);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public static void deleteTaskById(String id) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            mapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public static void deleteTaskByName(String name) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            TaskExample taskExample = new TaskExample();
            taskExample.createCriteria().andTaskNameEqualTo(name);
            mapper.deleteByExample(taskExample);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }
}
