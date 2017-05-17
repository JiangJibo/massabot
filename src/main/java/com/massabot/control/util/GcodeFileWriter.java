/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * 示教数据写入类
 * 
 * @since 2017年3月15日 下午3:31:38
 * @version $Id$
 * @author JiangJibo
 *
 */
public class GcodeFileWriter {

	private static Coordinates preCodn = new Coordinates();

	final static Logger LOGGER = LoggerFactory.getLogger(GcodeFileWriter.class);

	/**
	 * 将Gcode写入到指定文件
	 * 
	 * @param filePath
	 * @param gcodes
	 * @throws IOException
	 */
	public static void writeGcodesToFile(String filePath, String gcodes, boolean append) throws IOException {
		File file = new ClassPathResource(filePath).getFile();
		FileUtils.write(file, gcodes, "UTF-8", append);
	}

	/**
	 * 写入示教返回的Gcodes
	 * 
	 * @param gcodes
	 * @throws IOException
	 */
	public static void writeDemoGcode(String filePath, List<String> gcodes) throws IOException {
		writeDemonstrateGcode(filePath, gcodes, false);
	}

	/**
	 * 将示教数据写入指定文件
	 * 
	 * @param filePath
	 * @param gcodes
	 * @param append
	 *            是否在文件原有内容后添加
	 * @throws IOException
	 */
	public static void writeDemonstrateGcode(String filePath, List<String> gcodes, boolean append) throws IOException {
		File file = new File(filePath);
		List<String> fillingGcode = new ArrayList<String>(gcodes.size());
		Coordinates curCodn = new Coordinates(); // 当前的坐标值对象
		for (int i = 0; i < gcodes.size(); i++) {
			String codeLine = null;
			try {
				codeLine = formatGcode(gcodes.get(i), curCodn);
				if (codeLine == null) {
					continue;
				}
				preCodn.copyValues(curCodn);
			} catch (Exception e) {
				LOGGER.error("解析串口返回的示教数据:[{}]时出错,忽略当前Gcode", gcodes.get(i), e);
				continue;
			}
			fillingGcode.add(codeLine);
		}
		FileUtils.writeLines(file, fillingGcode, append);
	}

	private static String formatGcode(String gcodeLine, Coordinates codn) {
		boolean zeroLine = true;
		if (gcodeLine.endsWith("\n")) {
			gcodeLine = gcodeLine.substring(0, gcodeLine.length() - 1);
		}
		String newGcode = "G1 ";
		String[] splits = gcodeLine.split("\t", 6);
		for (int i = 0; i < splits.length; i++) {
			String codePrefix = null;
			if (i < 3) {
				codePrefix = Character.toString((char) (88 + i)).toString(); // A,B,C
			} else {
				codePrefix = Character.toString((char) (65 + i - 3)).toString(); // X,Y,Z
			}
			int coords = transformCode(splits[i]);
			if (coords > 0) {
				zeroLine = false;
			}
			coords = coords < 0 ? 0 : coords; // 如果遇到负数坐标,修正为0
			codn.setCoordinate(codePrefix, coords);
			newGcode = newGcode + codePrefix + coords;
		}
		int speed = Coordinates.calcurateSpeed(preCodn, codn);
		if (zeroLine || speed == 0) {
			return null;
		}
		return newGcode + "F" + speed + ",";
	}

	/**
	 * 将数值转换为坐标值
	 * 
	 * @param gcode
	 * @return
	 */
	private static int transformCode(String gcode) {

		return (int) (Integer.parseInt(gcode) * 0.032);
	}

	/**
	 * Gcode坐标值
	 * 
	 * @since 2017年3月30日 上午9:55:43
	 * @version $Id$
	 * @author JiangJibo
	 *
	 */
	private static class Coordinates {

		private int A;
		private int B;
		private int C;
		private int X;
		private int Y;
		private int Z;

		public void setCoordinate(String name, int num) {
			switch (name) {
			case "A":
				this.A = num;
				break;
			case "B":
				this.B = num;
				break;
			case "C":
				this.C = num;
				break;
			case "X":
				this.X = num;
				break;
			case "Y":
				this.Y = num;
				break;
			case "Z":
				this.Z = num;
				break;
			default:
				throw new IllegalArgumentException("不能为坐标轴:[" + name + "]赋:[" + num + "]值");
			}
		}

		public void copyValues(Coordinates target) {
			this.A = target.A;
			this.B = target.B;
			this.C = target.C;
			this.X = target.X;
			this.Y = target.Y;
			this.Z = target.Z;
		}

		public static int calcurateSpeed(Coordinates pre, Coordinates next) {
			int num = twice(pre.A, next.A) + twice(pre.B, next.B) + twice(pre.C, next.C) + twice(pre.X, next.X) + twice(pre.Y, next.Y) + twice(pre.Z, next.Z);
			return (int) (600 * Math.sqrt(num));
		}

		private static int twice(int a, int b) {
			return (int) Math.pow(a - b, 2);
		}

	}

}
