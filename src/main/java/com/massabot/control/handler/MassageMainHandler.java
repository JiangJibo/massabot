/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.codesender.model.UGSEvent.ControlState;
import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassageServiceType;
import com.massabot.control.constant.StateNodes;
import com.massabot.control.util.GcodeFileReader;
import com.massabot.control.util.GcodeSender;

/**
 * @since 2017年2月27日 下午2:18:06
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class MassageMainHandler {

	final static Logger LOGGER = LoggerFactory.getLogger(MassageMainHandler.class);

	@Autowired
	private GUIBackend backend;

	@Autowired
	private MassageStateHandler stateHandler;

	@Autowired
	private GcodeSender gcodeSender;

	private volatile List<String> sendingGcodes;

	/**
	 * 发送指定服务类型的Gcode
	 * 
	 * @param serviceType
	 * @throws Exception
	 */
	public void sendGcodes(MassageServiceType msType) throws Exception {
		// 如果运行结束或者断开过连接,重新开始选择类型及发送gcode
		if (StateNodes.CONNECTED == stateHandler.getStateNode()) {
			stateHandler.setMassageType(msType);
			stateHandler.setCurrentLine(0);
			sendingGcodes = GcodeFileReader.readGcodeFile(msType.filePath);
			stateHandler.setTotalLines(sendingGcodes.size());
		}
		stateHandler.setStateNode(StateNodes.RUNNING);
		LOGGER.info("当前行数为:[{}]", stateHandler.getCurrentLine());
		LOGGER.info("总行数为:[{}]", stateHandler.getTotalLines());
		gcodeSender.sendGcode(sendingGcodes);
	}

	/**
	 * 清除缓存
	 * 
	 * @throws InterruptedException
	 */
	public void clearCache() throws InterruptedException {
		stateHandler.cancleSend();
		this.sendingGcodes = null;
	}

	/**
	 * @return the currentType
	 */
	public int getCurrentType() {
		return stateHandler.getServiceType().code;
	}

	/*
	 * 
	 * 对直接操作电机驱动类的方法的封装
	 */

	/**
	 * 是否暂停中
	 * 
	 * @return
	 */
	public boolean isPaused() {
		return backend.isPaused();
	}

	public void setControlState(ControlState state) {
		backend.setControlState(state);
	}

	/**
	 * 暂停电机运行
	 * 
	 * @throws Exception
	 */
	public void pause() throws Exception {
		gcodeSender.setPaused(true);
	}

	/**
	 * 继续服务,从之前停止的Gcode代码行重新开始
	 * 
	 * @throws Exception
	 */
	public void resume() throws Exception {
		backend.setControlState(ControlState.COMM_SENDING_PAUSED);
		backend.pauseResume();
		stateHandler.setStateNode(StateNodes.RUNNING);
		gcodeSender.setPaused(false);
		sendGcodes(stateHandler.getServiceType());
	}

	/**
	 * 结束运行
	 * 
	 * @throws Exception
	 */
	public void abort() throws Exception {
		if (stateHandler.getStateNode() == StateNodes.RESET) {
			throw new MassaBotException("机械臂复位中,请稍等");
		}
		if (backend.isPaused()) { // 如果当前是暂停状态
			backend.cancel();
			stateHandler.cancleSend();
			stateHandler.setStateNode(StateNodes.CONNECTED);
		} else if (!gcodeSender.isPausing()) { // 如果当前既不是暂停状态,也不是正在暂停,则执行暂停
			gcodeSender.setAbort(true);
		}
		this.sendingGcodes = null;
	}

	public ControlState getControlState() {
		return backend.getControlState();
	}

	/**
	 * 查询手指当前温度
	 * 
	 * @return
	 * @throws Exception
	 */
	public Integer retrieveFingerTemp() throws Exception {
		gcodeSender.sendGcode("I\r\n");
		int temp = gcodeSender.getReturnTemp();
		stateHandler.setFingerTemp(temp);
		return temp;
	}

	/**
	 * 设置手指温度
	 * 
	 * @param temp
	 * @throws Exception
	 */
	public void setFingerTemp(int temp) throws Exception {
		// LOGGER.debug("当前线程名称为:[{}]", Thread.currentThread().getName());
		stateHandler.setFingerTemp(temp);
		gcodeSender.sendGcode("T" + temp + "\r\n");
	}

	public void sendVoiceWord(String voiceWord) throws Exception {
		gcodeSender.sendGcode(voiceWord);
	}

}
