/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.schedule.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.massabot.control.service.MassageConnectService;

/**
 * 定期执行,每5分钟检查用户是否超时未操作,如果是则断开连接防止电机长时间被空闲占用
 * 
 * @since 2017年3月31日 下午2:42:09
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class ConnectRecordsTask {

	final static Logger LOGGER = LoggerFactory.getLogger(ConnectRecordsTask.class);

	@Autowired
	private ConnectRecords connectRecords;

	@Autowired
	private MassageConnectService connectService;

	@Scheduled(initialDelay = 300000, fixedDelay = 300000)
	public void execute() {
		LOGGER.debug("在线程[{}]执行@Scheduled标识的方法", Thread.currentThread().getName());
		if (connectService.isSerialPortActive()) { // 如果电机工作中,则更新最近请求时间
			connectRecords.updateLastRequestTime();
			return;
		}
		if (connectRecords.isConnected() && connectRecords.isConnectTimeout()) { // 如果用户操作超时
			LOGGER.warn("用户{}分钟未操作,断开连接", connectRecords.getUnoperateTime());
			connectService.disConnect();
			connectRecords.cleanConnectCache();
		}
	}

}
