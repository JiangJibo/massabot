/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.massabot.control.constant.MassaBotConstant;
import com.massabot.control.util.GcodeFileWriter;

/**
 * @since 2017年3月22日 下午4:39:23
 * @version $Id$
 * @author JiangJibo
 *
 */
public class DemoFileTest {

	@Test
	public void testListFiles() {
		File dir = new File(MassaBotConstant.DEMO_FILE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			System.out.println(file.length());
			System.out.println(file.getName());
		}
	}

	@Test
	public void getFileCreatedTime() {
		File dir = new File(MassaBotConstant.DEMO_FILE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			getCreateTime(file);
		}
	}

	/**
	 * 读取文件创建时间
	 */
	public void getCreateTime(File file) {
		String filePath = file.getAbsolutePath();
		String strTime = null;
		try {
			Process p = Runtime.getRuntime().exec("cmd /C dir " + filePath + "/tc");
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.endsWith(".txt")) {
					strTime = line.substring(0, 17);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("创建时间    " + strTime);
		// 输出：创建时间 2009-08-17 10:21
	}

	@Test
	public void testCompareString() {
		String s1 = "2017/03/22  21:05";
		String s2 = "2017/03/22  20:20";
		System.out.println(s1.compareTo(s2));
	}

	@Test
	public void testCalcurateSpeed() throws IOException {
		String a = "100\t100\t520\t300\t450\t470";
		String b = "300\t220\t280\t690\t2000\t1900";
		String c = "1800\t968\t2630\t3587\t2687\t4985";
		String d = "3650\t3685\t2145\t1520\t1530\t1520";
		List<String> gcodes = Arrays.asList(a, b, c, d);
		GcodeFileWriter.writeDemoGcode("C:/Users/dell-7359/Desktop/demogcode/demo.txt", gcodes);
	}

}
