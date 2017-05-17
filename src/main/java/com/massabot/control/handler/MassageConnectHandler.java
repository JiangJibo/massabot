/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.control.constant.StateNodes;

/**
 * @since 2017年4月14日 上午11:36:40
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class MassageConnectHandler {

	@Autowired
	private GUIBackend backend;

	@Autowired
	private volatile MassageStateHandler stateHandler;

	/**
	 * 是否连接中
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return backend.isConnected();
	}

	/**
	 * 指定端口是否在使用中
	 * 
	 * @param portName
	 * @return
	 */
	public boolean isCurrentPortBusy(String portName) {
		return portName.equals(stateHandler.getPortName());
	}

	/**
	 * 尝试连接
	 * 
	 * @param firmware
	 * @param port
	 * @param baudRate
	 * @throws Exception
	 */
	public void connect(String firmware, String port, int baudRate) throws Exception {
		backend.connect(firmware, port, baudRate);
		stateHandler.setPortName(port);
		stateHandler.setStateNode(StateNodes.CONNECTED);
		// backend.getConnection().getCallBack().take();
		// 连接时会返回一个SerialPortEvent,从队列中拿出此Event,以不影响之后的指令发送
	}

	/**
	 * 断开连接
	 * 
	 * @throws Exception
	 */
	public void disconnect() throws Exception {
		backend.disconnect();
		stateHandler.setStateNode(StateNodes.DISCONNECTED);
	}

	public boolean isBackendActive() {
		return backend.isActive();
	}

	/**
	 * 清除缓存
	 * 
	 * @throws InterruptedException
	 */
	public void clearCache() throws InterruptedException {
		stateHandler.disConnect();
	}

}
