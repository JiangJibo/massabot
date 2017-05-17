/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.event;

import java.time.Clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.control.handler.MassageStateHandler;

/**
 * @since 2017年3月28日 下午1:58:59
 * @version $Id$
 * @author JiangJibo
 *
 */
public class MassaBotEventListener {

	final static Logger LOGGER = LoggerFactory.getLogger(MassaBotEventListener.class);

	@Autowired
	private volatile MassageStateHandler stateHandler;

	@Autowired
	private GUIBackend backend;

	/**
	 * 复位事件处理
	 * 
	 * @param event
	 * @throws Exception
	 */
	@Async
	@EventListener
	public void handlerResetEvent(MassaBotResetEvent event) throws Exception {
		System.out.println(Thread.currentThread().getName());
		long time1 = Clock.systemDefaultZone().millis();
		LOGGER.debug("开始复位机械臂!");
		event.doReset(backend, stateHandler);
		LOGGER.debug("结束复位机械臂,用时:[" + (Clock.systemDefaultZone().millis() - time1) / 1000 + "]秒");
	}

}
