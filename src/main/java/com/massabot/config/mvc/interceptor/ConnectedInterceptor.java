/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.massabot.config.mvc.AppMvcConfig;
import com.massabot.config.mvc.schedule.connect.ConnectRecords;
import com.massabot.config.root.exception.MassaBotException;

/**
 * 在同一时间只能有一台手机连接控制电机,通过手机设备ID来识别控制权限
 * 
 * @since 2017年3月31日 上午11:20:37
 * @version $Id$
 * @author JiangJibo
 *
 */
public class ConnectedInterceptor extends HandlerInterceptorAdapter {

	private static final String ANDROIDID = "androidId"; // 手机Header中存储的设备id的key

	final static Logger LOGGER = LoggerFactory.getLogger(ConnectedInterceptor.class);

	private final String[] includePatterns = { "/index/connection/**" };
	private final String[] excludePatterns = {};

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		ConnectRecords records = AppMvcConfig.getAppContext().getBean("connectRecords", ConnectRecords.class);
		String storedAndroidId = records.getAndroidId();
		String androidId = request.getHeader(ANDROIDID);
		if ("POST".equals(request.getMethod())) {
			if (null == storedAndroidId) {
				records.connect(androidId);
			} else if (!storedAndroidId.equals(androidId)) {
				LOGGER.warn("其他用户在电机运行时试图连接控制电机");
				throw new MassaBotException("其他用户正在使用电机,请稍后尝试连接");
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		if ("DELETE".equals(request.getMethod())) {
			AppMvcConfig.getAppContext().getBean("connectRecords", ConnectRecords.class).cleanConnectCache();
		}
	}

	/**
	 * @return the includePatterns
	 */
	public String[] getIncludePatterns() {
		return includePatterns;
	}

	/**
	 * @return the excludePatterns
	 */
	public String[] getExcludePatterns() {
		return excludePatterns;
	}

}
