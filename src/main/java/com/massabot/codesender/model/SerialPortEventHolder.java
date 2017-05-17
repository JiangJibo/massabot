/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.codesender.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.massabot.codesender.utils.HexBytesUtil;

import jssc.SerialPortEvent;

/**
 * @since 2017年3月21日 上午11:01:40
 * @version $Id$
 * @author JiangJibo
 *
 */
public class SerialPortEventHolder {

	final static Logger LOGGER = LoggerFactory.getLogger(SerialPortEventHolder.class);

	private volatile SerialPortEvent serialPortEvent; // 串口返回的时间

	private Integer resideNum; // 串口返回的剩余Gcode数量

	private String retString;

	public static final Integer DEFAULT_RESIDE_NUM = -1;

	/**
	 * @param serialPortEvent
	 * @param retByte
	 *            从串口返回的16位字节数组
	 */
	public SerialPortEventHolder(SerialPortEvent serialPortEvent, byte[] retByte) {
		this.serialPortEvent = serialPortEvent;
		this.resideNum = initResideNum(retByte);
		this.retString = new String(HexBytesUtil.encodeHex(retByte));
	}

	private Integer initResideNum(byte[] retByte) {
		int num = DEFAULT_RESIDE_NUM;
		try {
			String value = new String(HexBytesUtil.encodeHex(retByte));
			num = Integer.parseInt(value, 16);
		} catch (NumberFormatException e) {
			LOGGER.debug("解析串口返回剩余指令数量出错,默认使用[-1]标识");
		}
		return num;
	}

	/**
	 * @return the serialPortEvent
	 */
	public SerialPortEvent getSerialPortEvent() {
		return serialPortEvent;
	}

	/**
	 * @param serialPortEvent
	 *            the serialPortEvent to set
	 */
	public void setSerialPortEvent(SerialPortEvent serialPortEvent) {
		this.serialPortEvent = serialPortEvent;
	}

	/**
	 * @return the retResideNum
	 */
	public Integer getResideNum() {
		return resideNum;
	}

	/**
	 * @param retResideNum
	 *            the retResideNum to set
	 */
	public void setResideNum(Integer retResideNum) {
		this.resideNum = retResideNum;
	}

	/**
	 * @return the retString
	 */
	public String getRetString() {
		return retString;
	}

	public boolean isRXCHAR() {
		return serialPortEvent.isRXCHAR();
	}

}
