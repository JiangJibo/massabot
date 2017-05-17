/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.schedule.timedrun;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.massabot.control.constant.MassageServiceType;
import com.massabot.control.service.MassageConnectService;
import com.massabot.control.service.MassageMainService;

/**
 * @since 2017年4月20日 下午8:46:37
 * @version $Id$
 * @author JiangJibo
 *
 */
// @Component
public class TimingRunningTask {

	final static Logger LOGGER = LoggerFactory.getLogger(TimingRunningTask.class);

	@Autowired
	private MassageConnectService connectService;

	@Autowired
	private MassageMainService mainService;

	@Scheduled(initialDelay = 30000, fixedDelay = 600000)
	public void timedRunning() {
		List<String> portNames = connectService.getAvailablePortNames();
		if (portNames.isEmpty()) {
			return;
		}
		String portName = portNames.get(0);
		connectService.connect(portName);
		mainService.sendCommand(MassageServiceType.SHOULDER_MASSAGE);
	}

}
