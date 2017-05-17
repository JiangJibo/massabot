/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.handler;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.codesender.model.UGSEvent.ControlState;
import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassaBotConstant;
import com.massabot.control.constant.MassageServiceType;
import com.massabot.control.constant.StateNodes;
import com.massabot.control.util.GcodeFileReader;
import com.massabot.control.util.GcodeFileWriter;
import com.massabot.control.util.GcodeSender;

/**
 * 按摩示教案例处理,实现演示及复现
 * 
 * @since 2017年3月23日 下午2:08:08
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class MassageDemoCaseHandler {

	final static Logger LOGGER = LoggerFactory.getLogger(MassageDemoCaseHandler.class);

	@Autowired
	private MassageStateHandler stateHandler;

	@Autowired
	private GUIBackend backend;

	@Autowired
	private GcodeSender gcodeSender;

	private volatile String fileName;

	private List<String> sendingGcodes;

	/**
	 * 开始示教演示,给示教文件填充数据
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public void startProcessDemoFile(File file) throws Exception {
		backend.startReceivingMode();
		stateHandler.setMassageType(MassageServiceType.DEMO_MASSAGE);
		stateHandler.setStateNode(StateNodes.RUNNING);
		this.fileName = file.getName();
		stateHandler.setExtraMsg(fileName);
	}

	/**
	 * 暂停示教演示
	 * 
	 * @return
	 * @throws Exception
	 */
	public void pauseProcessDemoFile() throws Exception {
		backend.pauseReceivingMode();
		stateHandler.setStateNode(StateNodes.PAUSED);
	}

	/**
	 * 恢复示教演示
	 * 
	 * @return
	 * @throws Exception
	 */
	public void resumeProcessDemoFile() throws Exception {
		backend.startReceivingMode();
		stateHandler.setStateNode(StateNodes.RUNNING);
	}

	/**
	 * 结束示教演示,将示教数据写入示教文件
	 * 
	 * @return
	 */
	public void finishProcessDemoFile() throws Exception {
		backend.finishReceivingMode();
		List<String> gcodes = backend.getReceivedResult();
		GcodeFileWriter.writeDemoGcode(MassaBotConstant.DEMO_FILE_DIR + "/" + fileName, gcodes);
		stateHandler.setStateNode(StateNodes.CONNECTED);
	}

	/**
	 * 取消示教,放弃示教数据
	 * 
	 * @return
	 */
	public void cancleProcessDemoFile() throws Exception {
		backend.finishReceivingMode();
		stateHandler.setStateNode(StateNodes.CONNECTED);
	}

	/**
	 * 复现指定的示教案列
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public void reapperFromFile(String fileName) throws Exception {
		// 如果运行结束,重新开始选择示教案列及发送gcode
		if (StateNodes.CONNECTED == stateHandler.getStateNode()) {
			sendingGcodes = GcodeFileReader.readGcodeFile(fileName);
			stateHandler.setCurrentLine(0);
			stateHandler.setTotalLines(sendingGcodes.size());
			stateHandler.setMassageType(MassageServiceType.REAPPER_MASSAGE);
			stateHandler.setExtraMsg(fileName);
			this.fileName = fileName;
		}
		LOGGER.info("当前行数为:[{}]", stateHandler.getCurrentLine());
		LOGGER.info("总行数为:[{}]", stateHandler.getTotalLines());
		stateHandler.setStateNode(StateNodes.RUNNING);
		gcodeSender.sendGcode(sendingGcodes);
	}

	/**
	 * 暂停示教复现
	 * 
	 * @return
	 */
	public void pauseReapper() throws Exception {
		if (ControlState.COMM_SENDING == getControlState()) {
			gcodeSender.setPaused(true);
		}
	}

	/**
	 * 恢复示教复现
	 * 
	 * @return
	 * @throws Exception
	 */
	public void resumeReapper() throws Exception {
		backend.setControlState(ControlState.COMM_SENDING_PAUSED);
		backend.pauseResume();
		gcodeSender.setPaused(false);
		stateHandler.setStateNode(StateNodes.RUNNING);
		reapperFromFile(fileName);
	}

	/**
	 * 中途结束示教复现
	 * 
	 * @return
	 */
	public void abortReapper() throws Exception {
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

	/**
	 * 复位电机
	 */
	@Async
	public void resetDevice() {
		gcodeSender.resetDevice();
		stateHandler.setStateNode(StateNodes.CONNECTED);
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	public String getDemoName() {
		return fileName.substring(0, fileName.length() - 4);
	}

	public void setControlState(ControlState state) {
		backend.setControlState(state);
	}

	private ControlState getControlState() {
		return backend.getControlState();
	}

	/**
	 * 清除缓存,复位机械臂
	 * 
	 * @throws InterruptedException
	 */
	public void clearCache() throws InterruptedException {
		stateHandler.cancleSend();
		this.sendingGcodes = null;
	}

}
