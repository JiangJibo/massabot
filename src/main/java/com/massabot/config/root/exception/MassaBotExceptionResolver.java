/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.root.exception;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * @since 2017年3月21日 下午7:42:10
 * @version $Id$
 * @author JiangJibo
 *
 */
public class MassaBotExceptionResolver implements HandlerExceptionResolver {

	final static Logger LOGGER = LoggerFactory.getLogger(MassaBotExceptionResolver.class);

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if (ex instanceof MassaBotException) {
			MassaBotException e = (MassaBotException) ex;
			String message = e.getLocalizedMessage();
			LOGGER.warn(message, e);
			try {
				response.setHeader("Content-type", "text/html;charset=UTF-8");
				response.getOutputStream().write(message.getBytes(Charset.forName("UTF-8")));
				return new ModelAndView();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			LOGGER.error(ex.getLocalizedMessage(), ex);
		}
		return null;
	}

}
