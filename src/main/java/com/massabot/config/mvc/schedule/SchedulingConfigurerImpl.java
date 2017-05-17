/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.schedule;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 配置@Scheduled注解的线程池
 * 
 * @since 2017年4月15日 下午9:01:36
 * @version $Id$
 * @author JiangJibo
 *
 */
public class SchedulingConfigurerImpl implements SchedulingConfigurer {

	/* (non-Javadoc)
	 * @see org.springframework.scheduling.annotation.SchedulingConfigurer#configureTasks(org.springframework.scheduling.config.ScheduledTaskRegistrar)
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler taskSchedule = new ThreadPoolTaskScheduler();
		taskSchedule.setPoolSize(5);
		taskSchedule.setThreadNamePrefix("自定义@ScheduledThreadPool");
		taskSchedule.initialize();
		taskRegistrar.setTaskScheduler(taskSchedule);
	}

}
