/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.massabot.control.service.MassageDemoService;
import com.massabot.control.service.MassageStateService;

/**
 * 按摩示教Controller
 * 
 * @since 2017年3月25日 下午7:55:07
 * @version $Id$
 * @author JiangJibo
 *
 */
@RestController
@RequestMapping("/democase")
public class MassageDemoController {

	@Autowired
	private MassageDemoService demoService;

	@Autowired
	private MassageStateService stateService;

	/**
	 * 获取已有的示教文件名称
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<String> listDemoNames() {

		return demoService.getDemoCases();
	}

	/**
	 * 创建示教案列名称,即创建示教文件的名称
	 * 
	 * @param demoName
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String createDemoCase(@RequestParam String demoName) {

		return demoService.createNewDemoCase(demoName.substring(0, demoName.length() - 3));
	}

	/**
	 * 删除指定的示教案列名称,即删除示教文件
	 * 
	 * @param demoName
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public String deleteDemoCase(@RequestParam String demoName) {

		return demoService.deleteDemoCase(demoName.substring(0, demoName.length() - 3));
	}

	/**
	 * 开启示教演示
	 * 
	 * @param demoName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/writing", method = RequestMethod.PUT)
	public String startMassageDemo(@RequestParam String demoName) throws Exception {

		return demoService.doProcessDemoCase(demoName.substring(0, demoName.length() - 3));
	}

	/**
	 * 暂停示教演示
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/writing/suspension", method = RequestMethod.PUT)
	public String pauseMassageDemo() throws Exception {

		return demoService.pauseProcessDemoCase();
	}

	/**
	 * 恢复示教演示
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/writing/restoration", method = RequestMethod.PUT)
	public String resumeMassageDemo() throws Exception {

		return demoService.resumeProcessDemoCase();
	}

	/**
	 * 结束示教演示,将示教数据写入示教文件
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/writing", method = RequestMethod.POST)
	public String finishMassageDemo() throws Exception {

		return demoService.finishProcessDemoCase();
	}

	/**
	 * 复现指定的示教案列
	 * 
	 * @param demoName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reading", method = RequestMethod.POST)
	public String reapperDemoCase(@RequestParam String demoName) throws Exception {

		return demoService.startRepDemoCase(demoName.substring(0, demoName.length() - 3));
	}

	/**
	 * 暂停复现模式
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reading/suspension", method = RequestMethod.PUT)
	public String pauseRepMode() throws Exception {

		return demoService.pauseRepDemoCase();
	}

	/**
	 * 恢复复现模式
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reading/restoration", method = RequestMethod.PUT)
	public String resumeRepMode() throws Exception {

		return demoService.resumeRepDemoCase();
	}

	/**
	 * 中途结束复现模式
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reading", method = RequestMethod.DELETE)
	public String abortRepMode() throws Exception {

		return demoService.abortRepDemoCase();
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
	 * 复位机械臂
	 */
	@RequestMapping(value = "/resetting", method = RequestMethod.PUT)
	public String reset() {

		return demoService.resetDevice();
	}

}
