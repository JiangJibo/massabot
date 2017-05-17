/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service;

import java.util.List;

import com.massabot.control.constant.MassageServiceType;

/**
 * @since 2017年2月21日 下午4:02:00
 * @version $Id$
 * @author JiangJibo
 *
 */
public interface MassageMainService {

	/**
	 * 暂停服务
	 * 
	 * @return
	 */
	public String pause();

	/**
	 * 恢复服务
	 * 
	 * @return
	 */
	public String resume();

	/**
	 * 开始服务,发送指令
	 * 
	 * @return
	 */
	public String sendCommand(MassageServiceType serviceType);

	/**
	 * 结束服务
	 * 
	 * @return
	 */
	public String cancleSend();

	/**
	 * 设置手指温度
	 * 
	 * @param temp
	 * @return
	 */
	public String setFingerTemp(int temp);

	/**
	 * 获取所有可用的语音指令中文信息
	 * 
	 * @return
	 */
	public List<String> getVoiceText();

	/**
	 * 根据语音指令调节机器运行
	 * 
	 * @param voiceWord
	 * @return
	 */
	public String adjustByVoiceWord(String voiceWord);

}
