/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.massabot.config.root.exception.MassaBotException;
import com.massabot.control.constant.MassaBotConstant;

/**
 * 示教文件的读取,创建,删除者
 * 
 * @since 2017年3月25日 上午9:41:16
 * @version $Id$
 * @author JiangJibo
 *
 */
@Component
public class MassageDemoFileHandler {

	final static Logger LOGGER = LoggerFactory.getLogger(MassageDemoFileHandler.class);

	private static final Object FILE_CACHE_LOCK = new Object();

	private volatile Map<File, String> fileToTimeMapping = new HashMap<File, String>();

	/**
	 * 获取示教文件列表
	 * 
	 * @return
	 */
	public File[] getDemoFileNames() {
		File dir = new File(MassaBotConstant.DEMO_FILE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File[] files = dir.listFiles();
		if (files.length != 0) {
			LOGGER.debug("示教案列按照创建时间排序");
			Arrays.sort(files, (f1, f2) -> getCachedTime(f1).compareTo(getCachedTime(f2)));
		}
		return files;
	}

	/**
	 * 创建新的示教文件
	 * 
	 * @param fileName
	 * @return
	 */
	public void createDemoFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			throw new MassaBotException("示教案例已存在,请重新命名");
		}
		try {
			LOGGER.info("创建新的示教案列:[" + file.getAbsolutePath() + "]");
			file.createNewFile();
		} catch (IOException e) {
			throw new MassaBotException("创建示教案列时出现异常,请重新尝试", e);
		}
	}

	/**
	 * 删除指定的示教文件
	 * 
	 * @param fileName
	 * @return
	 */
	public void deleteDemoFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new MassaBotException("指定删除的示教案列不存在,请重新再试");
		}
		file.delete();
		fileToTimeMapping.remove(file);
	}

	/**
	 * 获取缓存的文件创建时间
	 * 
	 * @param file
	 * @return
	 */
	private String getCachedTime(File file) {
		String time = fileToTimeMapping.get(file);
		if (null == time) {
			synchronized (FILE_CACHE_LOCK) {
				time = fileToTimeMapping.get(file);
				if (time == null) {
					time = getFileCreatedTime(file);
					fileToTimeMapping.put(file, time);
				}
			}
		}
		return time;
	}

	/**
	 * 获取指定文件的创建时间
	 * 
	 * @param file
	 * @return
	 */
	private String getFileCreatedTime(File file) {
		String filePath = file.getAbsolutePath();
		String strTime = null;
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec("cmd /C dir " + filePath + "/tc");
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.endsWith(".txt")) {
					strTime = line.substring(0, 17);
					break;
				}
			}
		} catch (IOException e) {
			throw new MassaBotException("获取示教案列数据时出现错误,请稍后再试", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				throw new MassaBotException("获取示教案列数据时出现错误,请稍后再试", e);
			}
		}
		return strTime;
	}

}
