/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service;

import com.massabot.control.handler.MassageStateHandler.MassageServiceState;

/**
 * @since 2017年3月25日 上午10:48:28
 * @version $Id$
 * @author JiangJibo
 *
 */
public interface MassageStateService {

	/**
	 * APP重连时获取当前状态
	 * 
	 * @return
	 */
	public MassageServiceState getState();

	/**
	 * 获取当前运行时进度
	 * 
	 * @return
	 */
	public Integer getProgress();

}
