/**
 * Copyright(C) 2017 MassBot Co. Ltd. All rights reserved.
 *
 */
package com.massbot.test.concrete.controller;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.massabot.codesender.model.GUIBackend;
import com.massabot.control.util.GcodeFileWriter;
import com.massabot.control.util.GcodeSender;
import com.massbot.test.config.BaseControllerTest;

/**
 * @since 2017年2月21日 下午4:33:18
 * @version $Id$
 * @author JiangJibo
 *
 */
public class MassageControllerTest extends BaseControllerTest {

	@Autowired
	private GUIBackend backend;

	@Autowired
	private GcodeSender gcodeSender;

	/* (non-Javadoc)
	 * @see com.massbot.test.config.BaseControllerTest#init()
	 */
	@Override
	protected void init() {

	}

	@Test
	public void testGetConnect() throws Exception {
		this.postRequest("", "/massage/connection");
		this.postRequest("", "/massage/beginning");
	}

	@Test
	public void testGetAvailablePortNames() {
		String portNames = this.getRequest("/massage/portnames", "");
		System.out.println(portNames);
	}

	@Test
	public void testGetFingerTemp() throws Exception {
		backend.connect("TinyG", "COM9", 115200);
		String temp = this.getRequest("/main/fingertemp", "");
		System.out.println(temp);
	}

	@Test
	public void testGetGcodeBySerialPort() throws Exception {

		backend.connect("TinyG", "COM7", 115200);
		// backend.sendGcodeCommand("G28\n");
		backend.startReceivingMode();
		backend.finishReceivingMode();
		List<String> demonstrateGcodes = backend.getReceivedResult();
		GcodeFileWriter.writeDemoGcode("", demonstrateGcodes);
	}

	@Test
	public void sendCommand() throws Exception {
		backend.connect("TinyG", "COM5", 115200);
		backend.sendGcodeCommand("G28,");
		// backend.sendGcodeCommand("G1 A100 B60 C30 X16 Y10 Z10\r , G1 A50 B60 C30 X16 Y10 Z10");
		// backend.sendGcodeCommand("G28,");
		// backend.sendGcodeCommand("\r");
		/*backend.sendGcodeCommand("G1 A100\r");
		backend.sendGcodeCommand("G1 B100,");
		backend.sendGcodeCommand("G1 C100,");
		backend.sendGcodeCommand("G1 X100,");*/
		backend.sendGcodeCommand("G1 X10,G1 X20,G1 X30,G1 X40,");
		/*backend.sendGcodeCommand("G1 X20,");
		backend.sendGcodeCommand("G1 X30,");*/
		backend.sendGcodeCommand("G1 X40,");
		backend.sendGcodeCommand("G1 X50,");
		backend.sendGcodeCommand("G1 X60,");
		backend.sendGcodeCommand("G1 X100,");
		// backend.sendGcodeCommand("G1 Z100,");
		backend.disconnect();
	}

	@Test
	public void testGetLength() {
		String gcode = "0.000000	0.000000	0.000000";
		System.out.println(gcode.length());
	}

	@Test
	public void testGetTemp() throws Exception {
		backend.connect("TinyG", "COM9", 115200);
		gcodeSender.sendGcode("T0\r\n");
		System.out.println("温度:" + gcodeSender.getReturnTemp());
	}

}
