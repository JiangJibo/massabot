/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @since 2017年3月14日 下午6:23:58
 * @version $Id$
 * @author JiangJibo
 *
 */
public class ListRemoveTest {

	@Test
	public void textRemove() {
		List<String> list = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5"));
		String str = list.remove(2);
		System.out.println(list.toString());
	}

}
