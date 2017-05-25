/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.serialport;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.massabot.control.util.SerialPortConfigReader;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * @since 2017年3月11日 下午1:23:29
 * @version $Id$
 * @author JiangJibo
 *
 */
public class SerialPortTest {

	final static Logger LOGGER = LoggerFactory.getLogger(SerialPortTest.class);

	@Test
	public void testIteratorSerialPort() {
		String[] portNames = SerialPortList.getPortNames();
		for (String portName : portNames) {
			System.out.println(portName);
		}
	}

	@Test
	public void testAvailablePortNames() {
		List<String> portNames = SerialPortConfigReader.getAvailablePortNames();
		for (String portName : portNames) {
			SerialPort port = new SerialPort(portName);
			try {
				port.openPort();
				port.closePort();
			} catch (SerialPortException e) {
				LOGGER.error("端口名称:[" + portName + "]测试不可用!", e);
				continue;
			}
			System.out.println("端口名称:[" + portName + "]可用");
		}
	}

	@Test
	public void testRegex() {
		String regEx = "(\\d{1,3}\\.){3}\\d{1,3}";
		String ip = "A92.168.0.107";
		Pattern pattern = Pattern.compile(regEx);
		System.out.println(pattern.matcher(ip).matches());
	}

	@Test
	public void testSubstring() {
		String result = "192.168.1.113,COM3";
		System.out.println(result.substring(0, result.indexOf(",")));
		System.out.println(result.substring(result.indexOf(",") + 1));
	}

}
