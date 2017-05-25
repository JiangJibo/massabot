/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.junit.Test;

import com.massbot.test.config.BaseControllerTest;

/**
 * @since 2017年4月20日 下午2:48:45
 * @version $Id$
 * @author JiangJibo
 *
 */
public class Log4j2Test extends BaseControllerTest {

	/* (non-Javadoc)
	 * @see com.massbot.test.config.BaseControllerTest#init()
	 */
	@Override
	protected void init() {

	}

	/*static public Logger logger = LoggerFactory.getLogger(Log4j2Test.class);
	
	@Test
	public void logTC1() {
		logger.error("error");
		logger.debug("debug");
		logger.info("info");
		logger.trace("trace");
		logger.warn("warn");
		logger.error("error {}", "param");
	}
	*/
	@Test
	public void testGetAppenderName() throws InterruptedException {
		LoggerContext ctx = (LoggerContext) LogManager.getContext();
		Logger tmpLogger = ctx.getLogger("root");
		RollingFileAppender append = (RollingFileAppender) tmpLogger.getAppenders().get("desktopFile");
		File file = new File(append.getFileName());

		String fileName = file.getName();
		String prefix = fileName.substring(0, fileName.indexOf("."));
		String suffix = fileName.substring(fileName.indexOf("."));
		String newName = file.getParent() + "\\" + prefix + "-" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + suffix;
		System.out.println(newName);
		System.out.println(file.renameTo(new File(newName)));
		Thread.currentThread().sleep(1000);
		System.out.println(file.getAbsolutePath());
		System.out.println(file.getParent());
	}

}
