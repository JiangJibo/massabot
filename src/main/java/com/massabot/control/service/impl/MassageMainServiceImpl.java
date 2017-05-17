package com.massabot.control.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.massabot.codesender.model.UGSEvent.ControlState;
import com.massabot.config.mvc.schedule.voice.VoiceCommandTask;
import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassaBotConstant;
import com.massabot.control.constant.MassageServiceType;
import com.massabot.control.constant.VoiceWord;
import com.massabot.control.handler.MassageMainHandler;
import com.massabot.control.service.MassageMainService;

/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */

/**
 * @since 2017年2月21日 下午4:02:30
 * @version $Id$
 * @author JiangJibo
 *
 */
@Service
public class MassageMainServiceImpl implements MassageMainService {

	final static Logger LOGGER = LoggerFactory.getLogger(MassageMainServiceImpl.class);

	@Autowired
	private MassageMainHandler mainHandler;

	// @Autowired
	private VoiceCommandTask voiceTask;

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageService#sendCommand()
	 */
	@Override
	public String sendCommand(MassageServiceType serviceType) {
		LOGGER.info("用户指定开启：:[{}]服务", serviceType.label);
		try {
			mainHandler.sendGcodes(serviceType);
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("发送服务指令时出现错误,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageService#cancleSend()
	 */
	@Override
	public String cancleSend() {
		LOGGER.info("用户中断:[{}]服务", MassageServiceType.valueOf(mainHandler.getCurrentType()).label);
		try {
			mainHandler.abort();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("结束服务时发生错误,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageService#pause()
	 */
	@Override
	public String pause() {
		LOGGER.info("用户暂停:[{}]服务", MassageServiceType.valueOf(mainHandler.getCurrentType()).label);
		try {
			if (ControlState.COMM_SENDING == mainHandler.getControlState()) {
				mainHandler.pause();
			}
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("暂停服务时出现错误,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageService#resume()
	 */
	@Override
	public String resume() {
		LOGGER.info("用户恢复:[{}]服务", MassageServiceType.valueOf(mainHandler.getCurrentType()).label);
		try {
			mainHandler.resume();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("重新启动服务时出现错误,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageMainService#setFingerTemp(int)
	 */
	public String setFingerTemp(int temp) {
		LOGGER.info("用户设置指温为:[{}]", temp);
		try {
			mainHandler.setFingerTemp(temp);
		} catch (Exception e) {
			throw new MassaBotException("在设置手指温度为[" + temp + "]时出现错误", e);
		}
		return MassaBotConstant.SUCCESS_FLAG;
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageMainService#getVoiceText()
	 */
	@Override
	public List<String> getVoiceText() {
		LOGGER.info("用户获取可用的语音指令中文");
		/*List<VoiceCommand> voices = voiceTask.getVoices();
		if (voices.isEmpty()) {
			LOGGER.warn("没有可用的语音指令信息");
			return null;
		}
		List<String> texts = new ArrayList<String>();
		for (VoiceCommand voiceCommand : voices) {
			texts.add(voiceCommand.getText());
		}*/
		List<String> texts = new ArrayList<String>(VoiceWord.valueMap.keySet());
		LOGGER.info("目前可用的中文语音指令为:[{}]", texts.toString());
		return texts;
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageMainService#adjustByVoiceWord(java.lang.String)
	 */
	@Override
	public String adjustByVoiceWord(String voiceWord) {
		LOGGER.info("用户语音调节,语音信息为:[{}]", voiceWord);
		String code = null;
		for (VoiceWord voice : VoiceWord.values()) {
			if (voiceWord.contains(voice.label)) {
				if (code == null) {
					code = voice.code;
				} else {
					throw new MassaBotException("语音指令中包含了[" + code + "]和[" + voice.code + "]两个调节趋势");
				}
			}
		}
		if (code == null) {
			throw new MassaBotException("未能正确解析语音指令,请明确调整趋势");
		}
		try {
			mainHandler.sendVoiceWord(code);
		} catch (Exception e) {
			throw new MassaBotException("通过语音指令[" + voiceWord + "]调节按摩服务失败", e);
		}
		return MassaBotConstant.SUCCESS_FLAG;
	}

}
