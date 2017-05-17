/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassaBotConstant;
import com.massabot.control.handler.MassageConnectHandler;
import com.massabot.control.service.MassageConnectService;
import com.massabot.control.util.SerialPortConfigReader;

/**
 * @since 2017年4月14日 上午11:32:33
 * @version $Id$
 * @author JiangJibo
 *
 */
@Service
public class MassageConnectServiceImpl implements MassageConnectService {

	final static Logger LOGGER = LoggerFactory.getLogger(MassageConnectServiceImpl.class);

	@Autowired
	private MassageConnectHandler connectHandler;

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageConnectService#getAvailablePortNames()
	 */
	@Override
	public List<String> getAvailablePortNames() {
		return SerialPortConfigReader.getAvailablePortNames();
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageConnectService#connect(java.lang.String)
	 */
	@Override
	public boolean connect(String portName) {
		if (connectHandler.isConnected()) {
			if (connectHandler.isCurrentPortBusy(portName)) {
				return false;
			} else {
				throw new MassaBotException("开启连接时发生错误,请重新尝试");
			}
		}
		String firmware = SerialPortConfigReader.getFirmware();
		int baudRate = SerialPortConfigReader.getBaudrate();
		try {
			LOGGER.info("测试连接, firmware:[{}],portName:[{}],baudRate:[{}]", firmware, portName, baudRate);
			connectHandler.connect(firmware, portName, baudRate);
			boolean isConnected = connectHandler.isConnected();
			if (isConnected) {
				LOGGER.info("成功连接");
			}
			return true;
		} catch (Exception e) {
			throw new MassaBotException("开启连接时发生错误,请重新尝试", e);
		}

	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageConnectService#disConnect()
	 */
	@Override
	public String disConnect() {
		LOGGER.info("用户断开连接");
		try {
			if (connectHandler.isConnected()) {
				connectHandler.disconnect();
			}
			connectHandler.clearCache();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("断开连接时出现错误,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageConnectService#isSerialPortActive()
	 */
	@Override
	public boolean isSerialPortActive() {
		return connectHandler.isBackendActive();
	}

}
