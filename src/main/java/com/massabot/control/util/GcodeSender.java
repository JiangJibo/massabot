/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.util;

import java.io.File;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.codesender.model.SerialPortEventHolder;
import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.StateNodes;
import com.massabot.control.handler.MassageStateHandler;

/**
 * @since 2017年3月24日 上午11:15:13
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class GcodeSender {

	final static Logger LOGGER = LoggerFactory.getLogger(GcodeSender.class);

	private static final Integer REGISTER_GCODE_CAPACITY = 3; // 电机寄存器上能容纳的Gcode总数
	private static final Integer SEND_MAX_lENGTH = 2;

	private static final String RESET_GCODE = "G28,"; // 复位Gcode

	private static final String LAST_RETURN_STRING = "4F";// G28指令最后返回的值,也可能为"**4F"等

	private static final Integer RESUME_SPEED_AFTER_PAUSE = 3000; // 当从暂停恢复时,以指定速度移动到上次暂停的位置

	public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	@Autowired
	private volatile MassageStateHandler stateHandler;

	@Autowired
	private GUIBackend backend;

	private BlockingQueue<SerialPortEventHolder> queue;

	private volatile boolean isPaused;

	private volatile boolean isAbort;

	/**
	 * 发送指定文件的Gcode
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void sendGcodeFromFile(File file) throws Exception {
		if (file == null || !file.canRead() || file.length() == 0) {
			throw new MassaBotException("Gcode文件不可读或文件为空文件");
		}
		sendGcode(GcodeFileReader.readGcodeFile(file.getAbsolutePath()));
	}

	/**
	 * @param gcodes
	 * @throws Exception
	 */
	public void sendGcode(List<String> gcodes) throws Exception {

		if (queue == null) {
			queue = backend.getConnection().getCallBack();
		}
		queue.clear();

		Callable<Boolean> task = new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {

				int totalLines = stateHandler.getTotalLines();
				int currentLine = stateHandler.getCurrentLine();
				int resideGcodeNum = 0; // 电机寄存器上剩余的Gcode的数量
				int sendLength = 0; // 每次发送的条数

				while (currentLine < totalLines) {

					int num = REGISTER_GCODE_CAPACITY - resideGcodeNum; // 每次获取的Gcode的数量

					// 发送最大条数为2条,不能连续发送2条(如果上次发送了2条,这次最多能发送1条),可能会导致寄存器溢出,
					num = (sendLength == SEND_MAX_lENGTH || num < 1) ? 1 : num > SEND_MAX_lENGTH ? SEND_MAX_lENGTH : num;

					if (currentLine != 0 && sendLength == 0) { // 当前和循环次数是从暂停结束后的第一次发送,以指定速度移动到暂停位置
						LOGGER.debug("暂停后第一次发送指令,以恒定速度移动到暂停位置后再执行下一条指令");
						String lastSendedGcode = replaceGcodeSpeed(gcodes.get(currentLine), RESUME_SPEED_AFTER_PAUSE);
						sendMergedGcode(lastSendedGcode + gcodes.get(currentLine++));
						sendLength = SEND_MAX_lENGTH;
					} else {
						LOGGER.debug("当前行currentLine:[{}],准备读取:[{}]行指令", currentLine, num);
						sendLength = sendNextSeveralGcodes(gcodes, currentLine, num); // 实际发送的数量
						currentLine = currentLine + sendLength;
					}
					stateHandler.setCurrentLine(currentLine);
					resideGcodeNum = getResideGcodeNum();
					LOGGER.debug("电机寄存器内剩余Gcode条数为:[{}]", resideGcodeNum);

					if (isPaused) {
						LOGGER.info("用户暂停服务,当前服务类型为:[{}],Gcode行数为:[{}]", stateHandler.getServiceType().label, currentLine);
						return pauseSending();
					}

					if (isAbort) {
						LOGGER.info("用户中止服务:[{}]", stateHandler.getServiceType().label);
						return abortSending();
					}
				}
				return resetAfterSending();
			}
		};
		EXECUTOR.submit(task);
	}

	/**
	 * @return the isPaused
	 */
	public boolean isPausing() {
		return isPaused;
	}

	/**
	 * @param isPaused
	 *            the isPaused to set
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * @return the isAbort
	 */
	public boolean isAbort() {
		return isAbort;
	}

	/**
	 * @param isAbort
	 *            the isAbort to set
	 */
	public void setAbort(boolean isAbort) {
		this.isAbort = isAbort;
	}

	/**
	 * 从指定位置开始循环发送指定条数的指令
	 * 
	 * @param gcodes
	 * @param pivot
	 * @param size
	 * @return 实际发送的指令数量
	 * @throws Exception
	 */
	private Integer sendNextSeveralGcodes(List<String> gcodes, Integer pivot, Integer size) throws Exception {

		int totalLines = stateHandler.getTotalLines();
		StringBuffer sb = new StringBuffer();
		int num = size;
		while (totalLines > pivot && size > 0) {
			sb.append(gcodes.get(pivot.intValue()));
			size--;
			pivot++;
		}
		String gcode = sb.toString();
		LOGGER.debug("发送的指令为:[{}]", gcode);
		backend.sendGcodeCommand(gcode);
		return num - size;
	}

	/**
	 * 发送经过复合的指令
	 * 
	 * @param gcodes
	 * @throws Exception
	 */
	private void sendMergedGcode(String gcodes) throws Exception {
		LOGGER.debug("发送的指令为:[{}]", gcodes);
		backend.sendGcodeCommand(gcodes);
	}

	/**
	 * 发送单条指令
	 * 
	 * @param gcode
	 * @throws Exception
	 */
	public void sendGcode(String gcode) throws Exception {
		sendMergedGcode(gcode);
	}

	/**
	 * 替换Gcode的速度
	 * 
	 * @param gcode
	 * @param speed
	 * @return
	 */
	public String replaceGcodeSpeed(String gcode, Integer speed) {
		int speedFlagIndex = gcode.indexOf("F");
		return new StringBuffer(gcode).replace(speedFlagIndex + 1, gcode.indexOf(","), String.valueOf(speed)).toString();
	}

	/**
	 * 获取电机寄存器上剩余指令条数
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	private Integer getResideGcodeNum() {
		try {
			LOGGER.debug("当前队列内有:[{}]个返回指令", queue.size());

			SerialPortEventHolder eventHolder = null;
			do {
				eventHolder = queue.take(); // 遵循FIFO原则,获取最后一个存入Queue的元素
			} while (queue.size() > 0);

			if (eventHolder.isRXCHAR()) {
				int num = eventHolder.getResideNum();
				LOGGER.debug("寄存器内还有:[{}]条指令", num);
				return num != SerialPortEventHolder.DEFAULT_RESIDE_NUM ? num : REGISTER_GCODE_CAPACITY - 1; // 如果解析返回数量出错,则默认下次发送一条数据
			}

			return REGISTER_GCODE_CAPACITY - 1;
		} catch (Exception e) {
			throw new MassaBotException("电机未 返回正确的指令执行结果,请重新尝试", e);
		}
	}

	/**
	 * 暂停发送
	 * 
	 * @return
	 */
	private boolean pauseSending() {
		resetDevice();
		try {
			backend.pauseResume();
			stateHandler.setStateNode(StateNodes.PAUSED);
			isPaused = false;
			return true;
		} catch (Exception e) {
			throw new MassaBotException("暂停操作时发生异常,请重新尝试", e);
		}
	}

	/**
	 * 中止发送
	 * 
	 * @return
	 */
	private boolean abortSending() {
		resetDevice();
		try {
			backend.cancel();
			stateHandler.cancleSend();
			stateHandler.setStateNode(StateNodes.CONNECTED);
			isAbort = false;
			return true;
		} catch (Exception e) {
			throw new MassaBotException("中止操作时发生异常,请重新尝试", e);
		}
	}

	/**
	 * 在发送结束后复位
	 * 
	 * @return
	 */
	private boolean resetAfterSending() {
		resetDevice();
		stateHandler.cancleSend();
		stateHandler.setStateNode(StateNodes.CONNECTED);
		return true;
	}

	/**
	 * 复位机械臂
	 * 
	 * @param backend
	 * @param stateHandler
	 * @throws Exception
	 */
	public void resetDevice() {
		long time1 = Clock.systemDefaultZone().millis();
		try {
			stateHandler.setStateNode(StateNodes.RESET);
			backend.sendGcodeCommand(RESET_GCODE);
			BlockingQueue<SerialPortEventHolder> queue = backend.getConnection().getCallBack();
			do {

			} while (!queue.take().getRetString().contains(LAST_RETURN_STRING)); // 返回的字符串不含"4F"
		} catch (Exception e) {
			throw new MassaBotException("复位机械臂时发生异常,请重新尝试", e);
		}
		LOGGER.error("结束复位机械臂,用时:[{}]秒", (Clock.systemDefaultZone().millis() - time1) / 1000);
	}

}
