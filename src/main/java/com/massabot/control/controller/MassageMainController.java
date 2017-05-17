/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.controller;

import static com.massabot.control.constant.MassaBotConstant.FINGER_TEMP_MAX;
import static com.massabot.control.constant.MassaBotConstant.FINGER_TEMP_MIN;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassageServiceType;
import com.massabot.control.service.MassageConnectService;
import com.massabot.control.service.MassageMainService;
import com.massabot.control.service.MassageStateService;

/**
 * @since 2017年2月21日 下午4:15:03
 * @version $Id$
 * @author JiangJibo
 *
 */
@RestController
@RequestMapping("/main")
public class MassageMainController {

	@Autowired
	private MassageMainService mainService;

	@Autowired
	private MassageStateService stateService;

	@Autowired
	private MassageConnectService connectService;

	/**
	 * 启动指定的按摩服务
	 * 
	 * @param massageTypeCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/massage/{typecode}", method = RequestMethod.POST)
	public String sendCommand(@PathVariable Integer typecode) throws Exception {
		MassageServiceType msType = MassageServiceType.valueOf(typecode);
		if (null == msType) {
			throw new MassaBotException("未知的按摩服务类型");
		}
		return mainService.sendCommand(msType);
	}

	/**
	 * 結束服務
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/massage", method = RequestMethod.DELETE)
	public String terminate() throws Exception {
		return mainService.cancleSend();
	}

	/**
	 * 暂停服务
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/massage/suspension", method = RequestMethod.PUT)
	public String pause() throws Exception {
		return mainService.pause();
	}

	/**
	 * 继续服务
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/massage/restoration", method = RequestMethod.PUT)
	public String resume() throws Exception {
		return mainService.resume();
	}

	/**
	 * 获取当前进度
	 * 
	 * @return
	 */
	@RequestMapping(value = "/progress", method = RequestMethod.GET)
	public Integer getProgress() {
		return stateService.getProgress();
	}

	/**
	 * 设置手指温度
	 * 
	 * @param temp
	 * @return
	 */
	@RequestMapping(value = "/fingertemp/{temp}", method = RequestMethod.PUT)
	public String setFingerTemp(@PathVariable int temp) {
		if (temp < FINGER_TEMP_MIN || temp > FINGER_TEMP_MAX) {
			throw new MassaBotException("手指温度超出可调节范围");
		}
		return mainService.setFingerTemp(temp);
	}

	/**
	 * 用户获取所有可用的语音指令的中文信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/voicewords", method = RequestMethod.GET)
	public List<String> listVoiceText() {
		return mainService.getVoiceText();
	}

	/**
	 * 解析语音指令
	 * 
	 * @param voiceWords
	 * @return
	 */
	@RequestMapping(value = "/voicewords", method = RequestMethod.PUT)
	public String parseVoiceWords(@RequestParam String voiceWords) {
		return mainService.adjustByVoiceWord(voiceWords);
	}

	/**
	 * 断开连接
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connection", method = RequestMethod.DELETE)
	public String disConnect() throws Exception {
		return connectService.disConnect();
	}

}
