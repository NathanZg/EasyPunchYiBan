package com.ekertree.easypunch.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ekertree.easypunch.mapper.TaskMapper;
import com.ekertree.easypunch.mapper.UserMapper;
import com.ekertree.easypunch.pojo.Task;
import com.ekertree.easypunch.pojo.TaskExample;
import com.ekertree.easypunch.pojo.User;
import com.ekertree.easypunch.pojo.UserExample;
import com.ekertree.easypunch.utils.EncryptUtils;
import com.ekertree.easypunch.utils.Punch;
import com.ekertree.easypunch.utils.PunchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * ClassName: MybatisUtils
 * Description:
 * date: 2022/8/5 19:13
 *
 * @author Ekertree
 * @since JDK 1.8
 */
@Slf4j
public class UserService {


    public static List<User> getMorningPunchUserList() {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andIfPunchEqualTo(1)
                    .andMorningEqualTo(0);
            return mapper.selectByExample(userExample);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            sqlSession.close();
        }
        return null;
    }

    public static List<User> getNoonPunchUserList() {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andIfPunchEqualTo(1)
                    .andNoonEqualTo(0);
            return mapper.selectByExample(userExample);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
        return null;
    }

    public static List<User> getPunchUserList() {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andIfPunchEqualTo(1);
            return mapper.selectByExample(userExample);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
        return null;
    }

    public static void addUser(User user) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            if (StrUtil.isEmpty(user.getPhone())) {
                log.error("手机不能为空！");
                return;
            } else {
                User select = mapper.selectByPrimaryKey(user.getPhone());
                if (select != null) {
                    log.error("该用户已存在！");
                    return;
                }
            }
            if (StrUtil.isEmpty(user.getPassword())) {
                log.error("密码不能为空！");
                return;
            } else {
                user.setPassword(EncryptUtils.encode(user.getPassword()));
            }
            if (StrUtil.isEmpty(user.getAddress())) {
                log.error("地址不能为空！");
                return;
            }
            if (StrUtil.isEmpty(user.getHomeAddress())) {
                log.error("家庭地址不能为空！");
                return;
            }
            if (user.getIfSendEmail() == null) {
                user.setIfSendEmail(0);
            }
            if (user.getIfSendWecheat() == null) {
                user.setIfSendWecheat(0);
            }
            if (user.getIfPunch() == null) {
                user.setIfPunch(1);
            }
            if (user.getMorning() == null) {
                user.setMorning(0);
            }
            if (user.getNoon() == null) {
                user.setNoon(0);
            }
            if (StrUtil.isEmpty(user.getUserAgent())) {
                user.setUserAgent(PunchUtils.DEFAULT_USER_AGENT);
            }
            mapper.insert(user);
            Punch punch = new Punch();
            punch.updateUserSessionUrl(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public static void updateById(User user) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            if (StrUtil.isEmpty(user.getPhone())) {
                log.error("手机不能为空！");
                return;
            }
            if (StrUtil.isEmpty(user.getPassword())) {
                log.error("密码不能为空！");
                return;
            }
            if (StrUtil.isEmpty(user.getAddress())) {
                log.error("地址不能为空！");
                return;
            }
            if (StrUtil.isEmpty(user.getHomeAddress())) {
                log.error("家庭地址不能为空！");
                return;
            }
            if (StrUtil.isEmpty(user.getUserAgent())) {
                log.error("手机标识不能为空！");
                return;
            }
            if (user.getIfSendEmail() == null) {
                user.setIfSendEmail(0);
            }
            if (user.getIfSendWecheat() == null) {
                user.setIfSendWecheat(0);
            }
            if (user.getIfPunch() == null) {
                user.setIfPunch(1);
            }
            if (user.getMorning() == null) {
                user.setMorning(0);
            }
            if (user.getNoon() == null) {
                user.setNoon(0);
            }
            mapper.updateByPrimaryKey(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public static void deleteUserById(String id) {
        SqlSession sqlSession = null;
        try {
            sqlSession = BaseService.getSqlSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            mapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

}
