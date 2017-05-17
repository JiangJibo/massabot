/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 按摩服务的类型
 * 
 * @since 2017年3月9日 下午2:36:53
 * @version $Id$
 * @author JiangJibo
 *
 */
public enum MassageServiceType {

	UNDEFINED(-1, "未指定", ""),

	SHOULDER_MASSAGE(1, "肩部按摩", "resources/gcode/ShoulderMassageGcode.txt"),

	BACK_MASSAGE(2, "背部按摩", "resources/gcode/BackMassageGcode.txt"),

	WAIST_MASSAGE(3, "腰部按摩", "resources/gcode/WaistMassageGcode.txt"),

	DEMO_MASSAGE(10, "示教演示", "C:/Users/dell-7359/Desktop/demogcode"),

	REAPPER_MASSAGE(11, "示教复现", "C:/Users/dell-7359/Desktop/demogcode");

	public Integer code; // 类型代码
	public String label;
	public String filePath;

	/**
	 * @param type
	 * @param label
	 * @param filePath
	 * @param totalLines
	 */
	private MassageServiceType(Integer code, String label, String filePath) {
		this.code = code;
		this.label = label;
		this.filePath = filePath;
	}

	private static final Map<Integer, MassageServiceType> enumMap = new HashMap<Integer, MassageServiceType>(16);

	static {
		for (MassageServiceType type : MassageServiceType.values()) {
			enumMap.put(type.code, type);
		}
	}

	public static MassageServiceType valueOf(Integer code) {
		return enumMap.get(code);
	}

}
