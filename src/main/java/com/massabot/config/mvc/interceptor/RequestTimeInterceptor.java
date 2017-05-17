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
import com.massabot.control.constant.StateNodes;
import com.massabot.control.handler.MassageStateHandler;

/**
 * 在用户发送请求的时,更新最近请求时间
 * 
 * @since 2017年3月31日 下午3:00:57
 * @version $Id$
 * @author JiangJibo
 *
 */
public class RequestTimeInterceptor extends HandlerInterceptorAdapter {

	final static Logger LOGGER = LoggerFactory.getLogger(RequestTimeInterceptor.class);

	private final String[] includePatterns = null;
	private final String[] excludePatterns = { "/index/**" };

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		MassageStateHandler stateHandler = AppMvcConfig.getAppContext().getBean("massageStateHandler", MassageStateHandler.class);
		if (stateHandler.getStateNode() == StateNodes.DISCONNECTED) {
			throw new MassaBotException("长时间未操作已自动断开连接,请重新连接");
		}
		ConnectRecords connectDetail = AppMvcConfig.getAppContext().getBean("connectRecords", ConnectRecords.class);
		connectDetail.updateLastRequestTime();
		LOGGER.debug("更新用户最近操作时间:[" + connectDetail.getLastRequestTime() + "]");
		return true;
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
