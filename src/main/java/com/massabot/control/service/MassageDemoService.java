/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service;

import java.util.List;

/**
 * 示教Service
 * 
 * @since 2017年3月22日 下午4:30:20
 * @version $Id$
 * @author JiangJibo
 *
 */
public interface MassageDemoService {

	/**
	 * 获取已存在的示教文件的名称
	 * 
	 * @return
	 */
	public List<String> getDemoCases();

	/**
	 * 创建新的示教案列文件
	 * 
	 * @param fileName
	 * @return
	 */
	public String createNewDemoCase(String demoName);

	/**
	 * 删除指定的示教案列文件
	 * 
	 * @param fileName
	 * @return
	 */
	public String deleteDemoCase(String demoName);

	/**
	 * 开启示教演示,给示教文件填充示教数据
	 * 
	 * @param fileName
	 * @return
	 */
	public String doProcessDemoCase(String demoName);

	/**
	 * 暂停示教演示
	 * 
	 * @param fileName
	 * @return
	 */
	public String pauseProcessDemoCase();

	/**
	 * 继续示教演示
	 * 
	 * @return
	 */
	public String resumeProcessDemoCase();

	/**
	 * 结束示教演示
	 * 
	 * @return
	 */
	public String finishProcessDemoCase();

	/**
	 * 复现指定示教案列
	 * 
	 * @return
	 */
	public String startRepDemoCase(String demoName);

	/**
	 * 暂停示教复现
	 * 
	 * @return
	 */
	public String pauseRepDemoCase();

	/**
	 * 恢复示教复现
	 * 
	 * @return
	 */
	public String resumeRepDemoCase();

	/**
	 * 中途停止复现示教案列
	 * 
	 * @return
	 */
	public String abortRepDemoCase();

	/**
	 * 主动复位机械臂
	 */
	public String resetDevice();

}
