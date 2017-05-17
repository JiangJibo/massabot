/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.util;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jssc.SerialPortList;

/**
 * 固件读取工具类
 * 
 * @since 2017年3月8日 上午11:26:55
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class SerialPortConfigReader implements EnvironmentAware {

	final static Logger LOGGER = LoggerFactory.getLogger(SerialPortConfigReader.class);

	private static ConfigurableEnvironment environment;

	private static final String FIRMWARE = "com.massabot.serialport.firmware"; // 固件名称
	private static final String BAUDRATE = "com.massabot.serialport.baudrate"; // 波特率

	/* (non-Javadoc)
	 * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
	 */
	@Override
	public void setEnvironment(Environment environment) {
		SerialPortConfigReader.environment = (ConfigurableEnvironment) environment;
	}

	public static String getFirmware() {
		return environment.getProperty(FIRMWARE);
	}

	public static Integer getBaudrate() {
		return Integer.parseInt(environment.getProperty(BAUDRATE));
	}

	public static List<String> getAvailablePortNames() {
		return Arrays.asList(SerialPortList.getPortNames());
	}

}
