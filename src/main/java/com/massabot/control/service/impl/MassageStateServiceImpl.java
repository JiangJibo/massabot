/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.massabot.control.handler.MassageStateHandler;
import com.massabot.control.handler.MassageStateHandler.MassageServiceState;
import com.massabot.control.service.MassageStateService;

/**
 * @since 2017年3月25日 上午10:49:57
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class MassageStateServiceImpl implements MassageStateService {

	@Autowired
	private MassageStateHandler stateHandler;

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageStateService#getState()
	 */
	@Override
	public MassageServiceState getState() {
		return stateHandler.getMsState();
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageStateService#getProgress()
	 */
	@Override
	public Integer getProgress() {
		return stateHandler.getProgress();
	}

}
