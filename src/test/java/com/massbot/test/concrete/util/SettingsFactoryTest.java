/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import java.io.File;

import org.junit.Test;

import com.massabot.codesender.utils.SettingsFactory;

/**
 * @since 2017年3月15日 下午4:32:26
 * @version $Id$
 * @author JiangJibo
 *
 */
public class SettingsFactoryTest {

	@Test
	public void getContextDir() {
		File file = SettingsFactory.getSettingsDirectory();
		System.out.println(file.getAbsolutePath());
	}

}
