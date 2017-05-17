/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service;

import java.util.List;

/**
 * @since 2017年4月14日 上午11:31:13
 * @version $Id$
 * @author JiangJibo
 *
 */
public interface MassageConnectService {

	/**
	 * 获取所有可用的端口名称
	 * 
	 * @return
	 */
	public List<String> getAvailablePortNames();

	/**
	 * 连接串行端口,若连接超时则抛出异常,如当前是连接状态,则返回false,连接成功则返回true
	 * 
	 * @return
	 */
	public boolean connect(String portName);

	/**
	 * 终止连接，结束服务
	 * 
	 * @return
	 */
	public String disConnect();

	/**
	 * 串行端口是否在工作中
	 * 
	 * @return
	 */
	public boolean isSerialPortActive();

}
