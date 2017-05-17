/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.schedule.connect;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 用户连接电机的识别号及时间记录,以实现对电机的单一控制,及长时间未操作的自动断开复位功能
 * 
 * @since 2017年3月31日 下午2:09:19
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
@Scope("application")
public class ConnectRecords {

	final static Logger LOGGER = LoggerFactory.getLogger(ConnectRecords.class);

	private LocalTime connectTime; // 用户连接上的时间
	private LocalTime lastRequestTime; // 用户最后操作的时间

	private String androidId; // 用户的设备唯一识别标识(手机设备标识)

	public static final int DISCONNECT_DISTANCE = 300; // 用户在一定间隔内未对电机操控时,断开连接,单位秒

	/**
	 * 初始化连接时间
	 */
	public void initConnectTime() {
		this.connectTime = LocalTime.now();
	}

	/**
	 * 更新最近操控时间
	 */
	public void updateLastRequestTime() {
		lastRequestTime = LocalTime.now();
	}

	/**
	 * 获取连接时间
	 * 
	 * @return
	 */
	public String getConnectTime() {
		return connectTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * 获取最近操控时间
	 * 
	 * @return
	 */
	public String getLastRequestTime() {
		return lastRequestTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * @return the androidId
	 */
	public String getAndroidId() {
		return androidId;
	}

	/**
	 * @param androidId
	 *            the androidId to set
	 */
	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

	/**
	 * 设备连接
	 * 
	 * @param deviceId
	 */
	public void connect(String deviceId) {
		this.androidId = deviceId;
		LOGGER.debug("手机设备ID为:[" + deviceId + "]");
		this.connectTime = LocalTime.now();
		this.lastRequestTime = LocalTime.now();
	}

	/**
	 * 清空连接缓存
	 */
	public void cleanConnectCache() {
		this.connectTime = null;
		this.lastRequestTime = null;
		this.androidId = null;
	}

	/**
	 * 是否连接中
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return this.androidId != null;
	}

	/**
	 * 获取用户未操作时间
	 * 
	 * @return
	 */
	public int getUnoperateTime() {
		int hour = LocalTime.now().getHour() - lastRequestTime.getHour();
		int min = LocalTime.now().getMinute() - lastRequestTime.getMinute();
		return hour * 60 + min;
	}

	/**
	 * 查看用户操作是否超时
	 * 
	 * @return
	 */
	public boolean isConnectTimeout() {
		return LocalTime.now().minus(DISCONNECT_DISTANCE / 60, ChronoUnit.MINUTES).isAfter(lastRequestTime);
	}

}
