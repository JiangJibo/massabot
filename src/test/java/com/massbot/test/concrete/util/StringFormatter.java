/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import org.junit.Test;

/**
 * @since 2017年5月17日 下午9:08:07
 * @version $Id$
 * @author JiangJibo
 *
 */
public class StringFormatter {

	@Test
	public void testFormat() {
		System.out.println(String.format("你好啊%d,%s,%b", 10, "小明", true));
		System.out.println(String.format("%d/%d 的结果是: %d", 10, 5, 10 / 5));
	}

}
