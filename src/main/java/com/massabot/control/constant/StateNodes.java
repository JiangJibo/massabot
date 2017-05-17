/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2017年3月25日 上午11:02:38
 * @version $Id$
 * @author JiangJibo
 *
 */
public enum StateNodes {

	RESET(-1, "复位"),

	DISCONNECTED(0, "断开连接"),

	CONNECTED(1, "正常连接"),

	RUNNING(10, "正常运行"),

	PAUSED(11, "暂停"),

	TERMINATED(20, "结束");

	public Integer code;
	public String label;

	/**
	 * @param code
	 * @param label
	 */
	private StateNodes(Integer code, String label) {
		this.code = code;
		this.label = label;
	}

	private static final Map<Integer, StateNodes> valueMap = new HashMap<Integer, StateNodes>();

	static {
		for (StateNodes node : StateNodes.values()) {
			valueMap.put(node.code, node);
		}
	}

	public static StateNodes valueOf(Integer code) {
		return valueMap.get(code);
	}

}
