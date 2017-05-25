package com.massbot.test.config;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.massabot.config.root.log.Log4j2Configuration;

/**
 * @since 2017年1月5日 上午9:30:29
 * @version $Id$
 * @author JiangJibo
 *
 */
public class JUnit4ClassRunner extends SpringJUnit4ClassRunner {

	static {
		Log4j2Configuration.init();
	}

	/**
	 * @param clazz
	 * @throws InitializationError
	 */
	public JUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

}
