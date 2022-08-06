package com.ekertree.easypunch.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.ekertree.easypunch.mapper.TaskMapper;
import com.ekertree.easypunch.mapper.UserMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * ClassName: BaseService
 * Description:
 * date: 2022/8/6 12:56
 *
 * @author Ekertree
 * @since JDK 1.8
 */
@Slf4j
public class BaseService {

    //单实例对象，不然数据库连接池会初始化很多次
    private static SqlSessionFactory sqlSessionFactory;

    static {
        InputStream is = null;
        try {
            is = Resources.getResourceAsStream("mybatis-config.xml");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
    }

    public static SqlSession getSqlSession() {
        //true 自动提交事务 不需要再sqlSession.commit()
        return sqlSessionFactory.openSession(true);
    }
}
