/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @since 2017年2月28日 上午11:06:07
 * @version $Id$
 * @author JiangJibo
 *
 */
public class FillinGcode {

	@Test
	public void fillInGcodes() throws IOException {
		Resource resource = new ClassPathResource("resources/gcode/MassageGcode.txt");
		File file = new File("C:/Users/dell-7359/Desktop/robot/target/classes/resources/gcode/MassageGcode.txt");
		System.out.println(file.getAbsolutePath());
		OutputStream os = new FileOutputStream(file);
		for (int i = 49; i >= 0; i--) {
			String gcode = "G1 X" + 10 * i + "Y" + 10 * i + "Z" + 10 * i + "A" + 10 * i + "B" + 10 * i + "C" + 10 * i + ",";
			os.write(gcode.getBytes());
			System.out.println(gcode);
		}
		if (os != null) {
			os.close();
		}
	}

	@Test
	public void writeFile() throws IOException {
		File file = new File("C:/Users/dell-7359/Desktop/aaa.txt");
		System.out.println(file.getAbsolutePath());
		// OutputStream os = new FileOutputStream(file);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 50; i++) {
			// String gcode = "G1 X" + 10 * i + "\tY" + 10 * i + "\tZ" + 10 * i + "\tA" + 10 * i +
			// "B" + 10 * i + "C" + 10 * i + ",";
			String gcode = "G1 " + 10 * i + " ,";
			sb.append(gcode + "\n");
		}
		FileUtils.write(file, sb.toString());
	}

}
