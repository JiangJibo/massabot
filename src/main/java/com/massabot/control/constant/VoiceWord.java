/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户语音词表
 * 
 * @since 2017年4月25日 下午2:29:36
 * @version $Id$
 * @author JiangJibo
 *
 */
public enum VoiceWord {

	UPPER("上", "upper"),

	LOWER("下", "lower"),

	LEFT("左", "left"),

	RIGHT("右", "right"),

	FASTER("快", "faster"),

	SLOWER("慢", "slower"),

	LIGHTER("轻", "lighter"),

	POWER("重", "power");

	public String label;
	public String code;

	/**
	 * @param label
	 * @param code
	 */
	private VoiceWord(String label, String code) {
		this.label = label;
		this.code = code;
	}

	public static final Map<String, VoiceWord> valueMap = new HashMap<String, VoiceWord>();

	static {
		for (VoiceWord word : VoiceWord.values()) {
			valueMap.put(word.label, word);
		}
	}

	public static VoiceWord valueof(String label) {
		return valueMap.get(label);
	}

}
