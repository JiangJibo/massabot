/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.massabot.control.model.VoiceCommand;

/**
 * @since 2017年4月21日 下午3:20:19
 * @version $Id$
 * @author JiangJibo
 *
 */
@Repository
public interface VoiceCommandMapper {

	/**
	 * 查询所有的语音指令
	 * 
	 * @return
	 */
	public List<VoiceCommand> selectAll();

}
