package com.massabot.config.mvc.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

/**
 * 定时器配置类
 * 
 * @since 2017年2月13日 下午5:22:34
 * @version $Id$
 * @author JiangJibo
 *
 */
@Configuration
public class ScheduleConfiguration {

	/**
	 * @Scheduled注解解析Bean
	 * @return
	 */
	@Bean
	public ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor() {
		return new ScheduledAnnotationBeanPostProcessor();
	}

	/**
	 * 配置执行@Scheduled标识方法的线程池
	 * 
	 * @return
	 */
	@Bean
	public SchedulingConfigurerImpl schedulingConfigurer() {
		return new SchedulingConfigurerImpl();
	}

}