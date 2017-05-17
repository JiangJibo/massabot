/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.event;

import java.util.concurrent.BlockingQueue;

import org.springframework.context.ApplicationEvent;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.codesender.model.SerialPortEventHolder;
import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.StateNodes;
import com.massabot.control.handler.MassageStateHandler;

/**
 * 执行机械臂复原操作的事件,可自定义复原之后的工作
 * 
 * @since 2017年3月28日 下午1:56:23
 * @version $Id$
 * @author JiangJibo
 *
 */
public class MassaBotResetEvent extends ApplicationEvent {

	private static final long serialVersionUID = 3901154028831580546L;

	private static final String RESET_GCODE = "G28,";

	private static final String LAST_RET_STRING = "4F";// G28指令最后返回的值,也可能为"**4F"等

	/**
	 * @param source
	 *            在机械臂复原之后所做的工作,是一个函数式接口
	 */
	public MassaBotResetEvent(EventPostProcessor processor) {
		super(processor);
	}

	/**
	 * 复位机械臂
	 * 
	 * @param backend
	 * @param stateHandler
	 * @throws Exception
	 */
	public void doReset(GUIBackend backend, MassageStateHandler stateHandler) throws Exception {
		stateHandler.setStateNode(StateNodes.RESET);
		backend.sendGcodeCommand(RESET_GCODE);
		try {
			BlockingQueue<SerialPortEventHolder> queue = backend.getConnection().getCallBack();
			do {

			} while (queue.take().getRetString().indexOf(LAST_RET_STRING) < 0); // 返回的字符串不含"4F"
		} catch (Exception e) {
			throw new MassaBotException("复位机械臂时发生异常,请重新尝试");
		} finally {
			((EventPostProcessor) this.getSource()).doAfterEvent();
		}
	}

}
