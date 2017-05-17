package com.massabot.config.root.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 2017年1月7日 上午9:56:50
 * @version $Id$
 * @author JiangJibo
 *
 */
public class Log4j2Configuration {

	final static Logger LOGGER = LoggerFactory.getLogger(Log4j2Configuration.class);

	public static void init() {
		/*LoggerContext ctx = (LoggerContext) LogManager.getContext();
		org.apache.logging.log4j.core.Logger tmpLogger = ctx.getLogger("root");
		RollingFileAppender append = (RollingFileAppender) tmpLogger.getAppenders().get("desktopFile");
		File file = new File(append.getFileName());
		LOGGER.debug(file.getAbsolutePath());*/
	}
}
