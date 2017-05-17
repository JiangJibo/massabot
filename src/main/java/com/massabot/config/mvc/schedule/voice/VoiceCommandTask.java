/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.config.mvc.schedule.voice;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.massabot.control.mapper.VoiceCommandMapper;
import com.massabot.control.model.VoiceCommand;

/**
 * 定期地从公司服务器(目前从数据库)获取可用的语音指令
 * 
 * @since 2017年4月21日 下午4:46:04
 * @version $Id$
 * @author JiangJibo
 *
 */
// @Component
public class VoiceCommandTask {

	final static Logger LOGGER = LoggerFactory.getLogger(VoiceCommandTask.class);

	private volatile List<VoiceCommand> voices;

	@Autowired
	private VoiceCommandMapper voiceMapper;

	@PostConstruct
	public void initVoiceCommand() {
		voices = voiceMapper.selectAll();
	}

	/**
	 * 每3个小时更新语音指令
	 */
	@Scheduled(fixedDelay = 1800000)
	public void regularRefreshVoices() {
		voices = voiceMapper.selectAll();
	}

	/**
	 * @return the voices
	 */
	public List<VoiceCommand> getVoices() {
		return voices;
	}

}
