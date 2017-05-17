/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassaBotConstant;
import com.massabot.control.handler.MassageDemoCaseHandler;
import com.massabot.control.handler.MassageDemoFileHandler;
import com.massabot.control.service.MassageDemoService;

/**
 * @since 2017年3月22日 下午4:33:09
 * @version $Id$
 * @author JiangJibo
 *
 */
@Service
public class MassageDemoServiceImpl implements MassageDemoService {

	final static Logger LOGGER = LoggerFactory.getLogger(MassageDemoServiceImpl.class);

	private static final String FILE_SUFFIX = ".txt";

	@Autowired
	private MassageDemoCaseHandler demoCaseHandler;

	@Autowired
	private MassageDemoFileHandler demoFileHandler;

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#getDemoFileNames()
	 */
	@Override
	public List<String> getDemoCases() {
		LOGGER.info("用户获取示教案列列表");
		File[] files = demoFileHandler.getDemoFileNames();
		if (files.length == 0) {
			LOGGER.warn("示教案列列表为空");
			return null;
		}
		List<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			// 获取文件名,不含后缀,但是末位为文件是否为空的标识,若是空文件则+"(0)",非空+"(1)"
			fileNames.add(file.getName().substring(0, file.getName().lastIndexOf(".")) + "(" + (file.length() == 0 ? "0" : "1") + ")");
		}
		LOGGER.info("返回示教案列列表:[{}],(0):空文件;(1):非空文件", fileNames.toString());
		return fileNames;
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#createNewDemoFile(java.lang.String)
	 */
	@Override
	public String createNewDemoCase(String demoName) {
		LOGGER.info("用户创建新的示教案列,示教案列为:[{}]", demoName);
		String fileName = MassaBotConstant.DEMO_FILE_DIR + "/" + demoName + FILE_SUFFIX;
		demoFileHandler.createDemoFile(fileName);
		return MassaBotConstant.SUCCESS_FLAG;
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageDemoService#deleteDemoCase(java.lang.String)
	 */
	@Override
	public String deleteDemoCase(String demoName) {
		LOGGER.info("用户删除示教案列,示教案列为:[{}]", demoName);
		String fileName = MassaBotConstant.DEMO_FILE_DIR + "/" + demoName + FILE_SUFFIX;
		demoFileHandler.deleteDemoFile(fileName);
		return MassaBotConstant.SUCCESS_FLAG;
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageDemoService#doProcessDemoCase(java.lang.String)
	 */
	@Override
	public String doProcessDemoCase(String demoName) {
		LOGGER.info("用户开始示教演示,示教案列为:[{}]", demoName);

		File file = new File(MassaBotConstant.DEMO_FILE_DIR + "/" + demoName + FILE_SUFFIX);
		if (!file.exists() || !file.canRead()) {
			throw new MassaBotException("示教案列:[" + demoName + "]不存在或不可读取,请重新制定示教案列");
		}
		if (file.length() != 0) {
			throw new MassaBotException("示教案列:[" + demoName + "]已存在示教数据,不能覆盖,请重新选择示教案列");
		}
		try {
			demoCaseHandler.startProcessDemoFile(file);
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("开始示教案列:[" + demoName + "]演示时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#pauseProcessDemoCase(java.lang.String)
	 */
	@Override
	public String pauseProcessDemoCase() {
		LOGGER.info("用户暂停示教演示");
		try {
			demoCaseHandler.pauseProcessDemoFile();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("暂停示教:[" + demoCaseHandler.getDemoName() + "]演示时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#resumeProcessDemoCase()
	 */
	@Override
	public String resumeProcessDemoCase() {
		LOGGER.info("用户恢复示教演示");
		try {
			demoCaseHandler.resumeProcessDemoFile();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("恢复示教:[" + demoCaseHandler.getDemoName() + "]演示时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#finishProcessDemoCase()
	 */
	@Override
	public String finishProcessDemoCase() {
		LOGGER.info("用户结束示教演示,将示教数据写入示教文件");
		try {
			demoCaseHandler.finishProcessDemoFile();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("结束示教:[" + demoCaseHandler.getDemoName() + "]演示时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#startReapper(java.lang.String)
	 */
	@Override
	public String startRepDemoCase(String demoName) {
		LOGGER.info("用户选中复现示教案列:[{}]", demoName);
		String fileName = MassaBotConstant.DEMO_FILE_DIR + "/" + demoName + FILE_SUFFIX;
		try {
			demoCaseHandler.reapperFromFile(fileName);
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("开启示教:[" + demoName + "]复现时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.DemoService#pauseReapper()
	 */
	@Override
	public String pauseRepDemoCase() {
		LOGGER.info("用户暂停复现:[{}]案列", demoCaseHandler.getDemoName());
		try {
			demoCaseHandler.pauseReapper();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("暂停复现:[" + demoCaseHandler.getDemoName() + "]时出现错误,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageDemoService#resumeRepDemoCase()
	 */
	@Override
	public String resumeRepDemoCase() {
		LOGGER.info("用户恢复复现:[{}]案列", demoCaseHandler.getDemoName());
		try {
			demoCaseHandler.resumeReapper();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("恢复示教:[" + demoCaseHandler.getDemoName() + "]时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageDemoService#abortRepDemoCase()
	 */
	@Override
	public String abortRepDemoCase() {
		LOGGER.info("用户中止复现:[{}]案列", demoCaseHandler.getDemoName());
		try {
			demoCaseHandler.abortReapper();
			return MassaBotConstant.SUCCESS_FLAG;
		} catch (Exception e) {
			throw new MassaBotException("结束示教:[" + demoCaseHandler.getDemoName() + "]时出现异常,请重新尝试", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massabot.control.service.MassageDemoService#resetDevice()
	 */
	@Override
	public String resetDevice() {
		demoCaseHandler.resetDevice();
		return MassaBotConstant.SUCCESS_FLAG;
	}

}
