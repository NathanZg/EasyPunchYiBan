package com.ekertree.easypunch.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;

/**
 * ClassName: DruidDataSourceFactory
 * Description:
 * date: 2022/8/6 16:50
 *
 * @author Ekertree
 * @since JDK 1.8
 */
public class DruidDataSourceFactory extends PooledDataSourceFactory {
    public DruidDataSourceFactory() {
        this.dataSource = new DruidDataSource();
    }
}
