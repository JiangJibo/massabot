package com.massabot.control.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.massabot.config.root.exception.MassaBotException;

/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */

/**
 * @since 2017年3月1日 下午2:58:51
 * @version $Id$
 * @author JiangJibo
 *
 */
public class GcodeFileReader {

	final static Logger LOGGER = LoggerFactory.getLogger(GcodeFileReader.class);

	public static final String MASSAGE_FILE_PATH = "resources/gcode/MassageGcode.txt";

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(5);

	private static final Integer GCODES_ARRAY_LENGTH = 40;

	private final String[] gcodes = new String[GCODES_ARRAY_LENGTH];

	private final Integer onceReadLength = gcodes.length / 2;

	private volatile Integer rowNum = 1;

	public void init() throws Exception {
		LOGGER.debug("初始化,向数组内读满数据");
		this.readLines(0, gcodes.length);
	}

	public void readLines(int i, int length) {
		try {
			File file = new ClassPathResource(MASSAGE_FILE_PATH).getFile();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			int lineNum = 0;
			// 忽略已经读取过的行数
			int currentRow = rowNum;
			while (currentRow-- > 1) {
				br.readLine();
			}
			while ((line = br.readLine()) != null) {
				gcodes[i + lineNum] = line;
				lineNum++;
				if (lineNum == length) {
					LOGGER.debug("读取到第[{}]行:[{}]结束", rowNum, line);
					rowNum++;
					break;
				}
				Thread.currentThread();
				Thread.sleep(100);
				LOGGER.debug("读取了第:[{}]行数据:[{}]", rowNum, line);
				rowNum++;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 双线程实现边读边发送
	 * 
	 * @throws Exception
	 */
	public void sendGcode() throws Exception {

		Callable<String> task = new Callable<String>() {

			@Override
			public String call() throws Exception {
				for (int i = 0; i < gcodes.length;) {
					String gcode = gcodes[i];
					if (gcode == null) {
						LOGGER.debug("数据已经发送完毕。");
						break;
					}
					if (i == gcodes.length / 2) {
						EXECUTOR.submit(new Thread(() -> readLines(0, onceReadLength)));
					}
					if (i == 0 && gcodes[gcodes.length / 2] == null) {
						EXECUTOR.submit(new Thread(() -> readLines(gcodes.length / 2, onceReadLength)));
					}
					LOGGER.debug("当前发送的gcode:[{}]", gcode);
					Thread.currentThread();
					Thread.sleep(200);
					gcodes[i] = null;
					if (i == gcodes.length - 1) {
						LOGGER.debug("已到达数组的末端,从新指向数组的起始位置");
						i = 0;
					} else {
						i++;
					}
				}
				return null;
			}
		};
		EXECUTOR.submit(task);

	}

	/**
	 * 读取指定文件内的Gcode
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> readGcodeFile(String path) {
		Resource resource = null;
		if (path.indexOf(":") != -1) {
			resource = new FileSystemResource(path);
		} else {
			resource = new ClassPathResource(path);
		}
		List<String> gcodes = new ArrayList<String>(1000);
		BufferedReader bw = null;
		try {
			bw = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			String line;
			while ((line = bw.readLine()) != null) {
				/*if (line.endsWith(",")) {
					line = line.substring(0, line.length() - 1);
				}*/
				gcodes.add(line.trim());
			}
			LOGGER.debug("从{}读取gcode文件", path);
			return gcodes;
		} catch (IOException e) {
			throw new MassaBotException("[" + path + "]路径不可读", e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				throw new MassaBotException("读取按摩数据时出现错误", e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		GcodeFileReader reader = new GcodeFileReader();
		reader.init();
		reader.sendGcode();
	}

}
