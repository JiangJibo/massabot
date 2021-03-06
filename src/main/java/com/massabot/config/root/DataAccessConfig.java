/**
 * Copyright(C) 2016 Fugle Technology Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.root;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @since 2016年12月5日 下午5:24:24
 * @version $Id$
 * @author JiangJibo
 *
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DataAccessConfig {

	private static final String driverClassName = "com.mysql.jdbc.Driver";
	private static final String username = "root";
	private static final String password = "lanboal";
	// MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
	// 避免中文乱码要指定useUnicode和characterEncoding
	// 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
	// 下面语句之前就要先创建javademo数据库
	private static final String url = "jdbc:mysql://localhost:3306/massabot?useUnicode=true&characterEncoding=UTF8&useSSL=false";

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setMaxTotal(50);
		dataSource.setMinIdle(5);
		dataSource.setMaxIdle(10);
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		// 配置MapperConfig
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		// 这个配置使全局的映射器启用或禁用缓存
		configuration.setCacheEnabled(true);
		// 允许 JDBC 支持生成的键，需要适合的驱动（如MySQL，SQL Server，Sybase ASE）。
		// 如果设置为 true 则这个设置强制生成的键被使用，尽管一些驱动拒绝兼容但仍然有效（比如 Derby）。
		// 但是在 Oracle 中一般不需要它，而且容易带来其它问题，比如对创建同义词DBLINK表插入时发生以下错误：
		// "ORA-22816: unsupported feature with RETURNING clause" 在 Oracle
		// 中应明确使用 selectKey 方法
		configuration.setUseGeneratedKeys(false);
		// 配置默认的执行器。SIMPLE 执行器没有什么特别之处；REUSE 执行器重用预处理语句；BATCH 执行器重用语句和批量更新
		configuration.setDefaultExecutorType(ExecutorType.REUSE);
		// 全局启用或禁用延迟加载，禁用时所有关联对象都会即时加载
		configuration.setLazyLoadingEnabled(false);
		// 设置SQL语句执行超时时间缺省值，具体SQL语句仍可以单独设置
		configuration.setDefaultStatementTimeout(5000);
		sqlSessionFactoryBean.setConfiguration(configuration);
		// 匹配多个 MapperConfig.xml, 使用mappingLocation属性
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:com/massabot/**/*Mapper.xml"));
		return sqlSessionFactoryBean.getObject();
	}
}
