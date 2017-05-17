/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.massabot.control.constant.MassaBotConstant;
import com.massabot.control.service.MassageConnectService;
import com.massabot.control.service.MassageStateService;

/**
 * @since 2017年4月14日 上午11:28:21
 * @version $Id$
 * @author JiangJibo
 *
 */
@RestController
@RequestMapping("/index")
public class MassageIndexController {

	@Autowired
	private MassageStateService stateService;

	@Autowired
	private MassageConnectService connectService;

	/**
	 * 获取可连接的端口名称
	 * 
	 * @return
	 */
	@RequestMapping(value = "/portnames", method = RequestMethod.GET)
	public List<String> getPortNames() {

		return connectService.getAvailablePortNames();
	}

	/**
	 * 连接指定端口,开启连接
	 * 
	 * @param portName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/connection/{portName}", method = RequestMethod.POST)
	public Object getConnect(@PathVariable String portName) throws Exception {
		// 如果当前是连接状态,则返回当前状态的详细信息
		return connectService.connect(portName) ? MassaBotConstant.SUCCESS_FLAG : stateService.getState();
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
