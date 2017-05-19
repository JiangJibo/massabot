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

	UPPER("上", "G1 A45B31C34X104Y87Z2000,"),

	LOWER("下", "G1 A60B44C36X136Y108Z2000,"),

	LEFT("左", "G1 A73B53C40X163Y128Z2000,"),

	RIGHT("右", "G1 A92B69C53X211Y168Z2000,"),

	FASTER("快", "G1 A95B79C63X230Y196Z2000,"),

	SLOWER("慢", "G1 A106B106C67X271Y239Z2000,"),

	LIGHTER("轻", "G1 A125B134C99X271Y248Z2000,"),

	POWER("重", "G1 A21B177C122X191Y263Z2000,");

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
