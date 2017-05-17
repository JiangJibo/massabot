/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massabot.control.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @since 2017年4月25日 上午9:07:48
 * @version $Id$
 * @author JiangJibo
 *
 */
public class NetAdressUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 获取机器名
		String hostName = getLocalMachineInfo("Host Name . . . . . . . . . . . . :");
		System.out.println("机器名：" + hostName);
		// 获取网卡型号
		String desc = getLocalMachineInfo("Description . . . . . . . . . . . :");
		System.out.println("网卡型号：" + desc);
		// 获取MAC地址
		String mac = getLocalMachineInfo("Physical Address. . . . . . . . . :");
		System.out.println("MAC地址：" + mac);
		// 获取IP地址
		String ip = getLocalMachineInfo("IPv4");
		System.out.println("IPv4  地址：" + ip);
		// 获取子网掩码
		String subnetMask = getLocalMachineInfo("Subnet Mask . . . . . . . . . . . :");
		System.out.println("子网掩码：" + subnetMask);
		// 获取默认网关
		String defaultGateway = getLocalMachineInfo("Default Gateway . . . . . . . . . :");
		System.out.println("机器名：" + defaultGateway);
		// 获取DNS服务器
		String DNSServers = getLocalMachineInfo("DNS Servers . . . . . . . . . . . :");
		System.out.println("机器名：" + DNSServers);
	}

	static String getLocalMachineInfo(String str) {
		String line = "";
		int n;
		boolean started = false;
		try {
			Process ps = Runtime.getRuntime().exec("cmd /c ipconfig");
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), "gbk"));
			while (null != (line = br.readLine())) {
				if (line.indexOf("Wi-Fi") != -1) {
					started = true;
				}
				if (line.indexOf(str) != -1 && started) {
					n = line.indexOf(":");
					line = line.substring(n + 2);
					break;
				}
			}
			ps.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

}
