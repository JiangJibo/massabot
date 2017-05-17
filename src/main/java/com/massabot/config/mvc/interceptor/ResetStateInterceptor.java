/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.massabot.config.mvc.AppMvcConfig;
import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.StateNodes;
import com.massabot.control.handler.MassageStateHandler;

/**
 * 在机械臂复位过程中,不能重新对电机发出指令
 * 
 * @since 2017年3月29日 下午5:05:16
 * @version $Id$
 * @author JiangJibo
 *
 */
public class ResetStateInterceptor extends HandlerInterceptorAdapter {

	private final String[] includePatterns = { "/main/massage/**", "/index/connection/**", "/democase/reading/**" };
	private final String[] excludePatterns = {};

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		MassageStateHandler stateHandler = AppMvcConfig.getAppContext().getBean("massageStateHandler", MassageStateHandler.class);
		if (stateHandler.getStateNode() == StateNodes.RESET) {
			throw new MassaBotException("机械臂正在复位中,请等待复位完毕后再尝试下一步操作");
		}
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
