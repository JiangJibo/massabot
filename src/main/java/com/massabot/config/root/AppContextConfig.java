package com.massabot.config.root;

import java.util.concurrent.ThreadPoolExecutor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.codesender.utils.SettingsFactory;

/**
 * @since 2017年1月10日 上午8:52:37
 * @version $Id$
 * @author JiangJibo
 *
 */
@EnableAsync
@MapperScan("com.massabot.**.mapper")
@PropertySource(value = { "classpath:resources/SerialPortConfig.properties" })
@ComponentScan(basePackages = { "com.massabot.config.root" })
public class AppContextConfig {

	@Bean
	public ConversionService conversionService() {
		return new GenericConversionService();
	}

	/**
	 * 定义操作机器人的bean
	 * 
	 * @return
	 */
	@Bean
	public static GUIBackend backend() {
		GUIBackend backend = new GUIBackend();
		try {
			backend.applySettings(SettingsFactory.loadSettings());
		} catch (Exception e) {

		}
		return backend;
	}

	/**
	 * 对@Async注解的支持,定义线程池以异步执行
	 * 
	 * @return
	 */
	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setKeepAliveSeconds(300);
		executor.setQueueCapacity(20);
		executor.setThreadNamePrefix("自定义AsyncThreadPool");
		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

}
