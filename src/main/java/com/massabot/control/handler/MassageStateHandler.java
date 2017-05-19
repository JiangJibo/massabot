/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.handler;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.massabot.control.constant.MassageServiceType;
import com.massabot.control.constant.StateNodes;
import com.massabot.control.util.GcodeSender;

/**
 * 按摩状态
 * 
 * @since 2017年3月23日 下午2:09:38
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class MassageStateHandler {

	private volatile int currentLine;
	private volatile int totalLines;

	@Autowired
	private GcodeSender gcodeSender;

	private volatile MassageServiceState msState = new MassageServiceState();

	public void setPortName(String portName) {
		this.msState.setCurrentPortName(portName);
	}

	public String getPortName() {
		return this.getMsState().getCurrentPortName();
	}

	public void setStateNode(StateNodes stateNode) {
		Assert.notNull(stateNode, "状态节点不能为空");
		this.msState.setStateNodeCode(stateNode.code);
	}

	public StateNodes getStateNode() {
		return StateNodes.valueOf(this.msState.getStateNodeCode());
	}

	public void setMassageType(MassageServiceType type) {
		this.msState.setCurrentType(type.code);
	}

	public MassageServiceType getServiceType() {
		return MassageServiceType.valueOf(this.msState.getCurrentType());
	}

	private void setProgress(int progress) {
		this.msState.setProgress(progress);
	}

	public Integer getProgress() {
		return totalLines == 0 ? null : currentLine * 100 / totalLines;
	}

	public void setExtraMsg(String msg) {
		this.msState.setExtraMsg(msg);
	}

	public String getExtraMsg() {
		return this.msState.getExtraMsg();
	}

	public void setFingerTemp(int temp) {
		this.msState.setFingerTemp(temp);
	}

	public int getFingerTemp() {
		return this.msState.getFingerTemp();
	}

	public void cancleSend() {
		this.setCurrentLine(0);
		this.setProgress(0);
		this.setExtraMsg(null);
		this.setMassageType(MassageServiceType.UNDEFINED);
	}

	public void disConnect() {
		this.setFingerTemp(0);
		this.setPortName(null);
		cancleSend();
	}

	/**
	 * @return the currentLine
	 */
	public int getCurrentLine() {
		return currentLine;
	}

	/**
	 * @param currentLine
	 *            the currentLine to set
	 */
	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}

	/**
	 * @return the totalLines
	 */
	public int getTotalLines() {
		return totalLines;
	}

	/**
	 * @param totalLines
	 *            the totalLines to set
	 */
	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}

	/**
	 * @return the msState
	 */
	public MassageServiceState getMsState() {
		if (totalLines != 0) {
			this.msState.setProgress(currentLine * 100 / totalLines);
		}
		return msState;
	}

	/**
	 * 当前按摩服务的状态
	 * 
	 * @since 2017年3月8日 下午1:46:28
	 * @version $Id$
	 * @author JiangJibo
	 *
	 */
	public static class MassageServiceState implements Serializable {

		private static final long serialVersionUID = -2584016421815133454L;

		private volatile String currentPortName; // 当前使用的串口名称
		private volatile int stateNodeCode; // 当前的状态节点
		private volatile int currentType; // 服务类型
		private volatile int progress; // 当前进度
		private volatile int fingerTemp; // 手指温度
		private volatile String extraMsg; // 额外的信息

		/**
		 * @return the currentPortName
		 */
		public String getCurrentPortName() {
			return currentPortName;
		}

		/**
		 * @param currentPortName
		 *            the currentPortName to set
		 */
		private void setCurrentPortName(String currentPortName) {
			this.currentPortName = currentPortName;
		}

		/**
		 * @return the stateNodeCode
		 */
		public int getStateNodeCode() {
			return stateNodeCode;
		}

		/**
		 * @param stateNodeCode
		 *            the stateNodeCode to set
		 */
		private void setStateNodeCode(int stateNodeCode) {
			this.stateNodeCode = stateNodeCode;
		}

		/**
		 * @return the currentType
		 */
		public int getCurrentType() {
			return currentType;
		}

		/**
		 * @param currentType
		 *            the currentType to set
		 */
		private void setCurrentType(int currentType) {
			this.currentType = currentType;
		}

		/**
		 * @return the progress
		 */
		public int getProgress() {
			return progress;
		}

		/**
		 * @param progress
		 *            the progress to set
		 */
		private void setProgress(int progress) {
			this.progress = progress;
		}

		/**
		 * @return the extraMsg
		 */
		public String getExtraMsg() {
			return extraMsg;
		}

		/**
		 * @param extraMsg
		 *            the extraMsg to set
		 */
		private void setExtraMsg(String extraMsg) {
			this.extraMsg = extraMsg;
		}

		/**
		 * @return the fingerTemp
		 */
		public int getFingerTemp() {
			return fingerTemp;
		}

		/**
		 * @param fingerTemp
		 *            the fingerTemp to set
		 */
		private void setFingerTemp(int fingerTemp) {
			this.fingerTemp = fingerTemp;
		}

		/**
		 * 服务状态的构造类
		 * 
		 * @since 2017年3月8日 下午1:52:06
		 * @version $Id$
		 * @author JiangJibo
		 *
		 *//*
			public static class StateBuilder {
			
			private int currentType;
			private int currentLine = 0;
			private boolean isActive = false;
			
			public static StateBuilder instance() {
				return new StateBuilder();
			}
			
			public StateBuilder currentType(int currentType) {
				this.currentType = currentType;
				return this;
			}
			
			public StateBuilder currentLine(int currentLine) {
				this.currentLine = currentLine;
				return this;
			}
			
			public StateBuilder isActive(boolean isActive) {
				this.isActive = isActive;
				return this;
			}
			
			public MassageServiceState build() {
				ServiceType type = ServiceType.valueOf(currentType);
				Assert.notNull(type, "必须指定按摩服务类型!");
				MassageServiceState state = new MassageServiceState();
				state.currentType = currentType;
				state.currentLine = currentLine;
				state.isActive = isActive;
				state.gcodeFilePath = type.filePath;
				state.totalLines = type.totalLines;
				return state;
			}
			
			}*/

	}

}
