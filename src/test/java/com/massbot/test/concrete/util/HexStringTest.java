/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import org.junit.Test;

/**
 * @since 2017年3月16日 下午3:50:45
 * @version $Id$
 * @author JiangJibo
 *
 */
public class HexStringTest {

	@Test
	public void testHexString() {
		String hex = Integer.toHexString(500000);
		System.out.println(hex);
	}

	@Test
	public void testHexToTen() {
		Integer num = Integer.parseInt("7a120", 16);
		System.out.println(num);
	}

	@Test
	public void testTransfor() {
		System.out.println(Integer.parseInt("4F", 16));

	}

	@Test
	public void getHostIP() {
		System.out.println(HexStringTest.getHostAdress("http://192.168.1.106:8080"));
	}

	public static String getHostAdress(String requestUrl) {
		return requestUrl.subSequence(requestUrl.indexOf("//") + 2, requestUrl.lastIndexOf(":")).toString();
	}

	@Test
	public void testStrix() {
		String str = "\naaaa";
		System.out.println(str);
		System.out.println(str.substring(1, str.length()));
	}

	@Test
	public void testEmptyString() {
		String str = "";
		System.out.println(str.length());
	}

	@Test
	public void testReplaceChar() {
		String curDemoCaseName = "第一次示教(0)";
		int length = curDemoCaseName.length();
		System.out.println(new StringBuffer(curDemoCaseName).replace(length - 2, length - 1, "1").toString());
	}

	@Test
	public void testModifyInteger() {
		Integer in = new Integer(4);
		modifyInteger(in);
		System.out.println(in);
	}

	private Integer modifyInteger(Integer in) {
		return in - 1;
	}

	@Test
	public void testModifyString() {
		String str = new String("aaa");
		modifyString(str);
		System.out.println(str);
	}

	private String modifyString(String str) {
		return "*" + str + "*";
	}

}
