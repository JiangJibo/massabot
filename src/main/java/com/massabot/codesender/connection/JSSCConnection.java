/*
 * A serial connection object implementing the connection API.
 */

/*
    Copywrite 2015 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.massabot.codesender.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.massabot.codesender.model.SerialPortEventHolder;
import com.massabot.codesender.utils.HexBytesUtil;
import com.massabot.config.root.exception.MassaBotException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author wwinder
 */
public class JSSCConnection extends Connection implements SerialPortEventListener {

	final static Logger LOGGER = LoggerFactory.getLogger(JSSCConnection.class);

	private static final BlockingQueue<SerialPortEventHolder> queue = new ArrayBlockingQueue<SerialPortEventHolder>(64);

	private volatile boolean useReceivedMode;
	private volatile boolean startReceivingMode; // 是否开启单向接受模式

	private List<String> receivedGcodes; // 用于存储示教返回的gcode

	@Deprecated
	private String lineTerminator;

	// General variables
	private SerialPort serialPort;
	private StringBuilder inputBuffer;

	public JSSCConnection() {
		this("\r\n");
	}

	public JSSCConnection(String terminator) {
		lineTerminator = terminator;
	}

	@Deprecated
	public void setLineTerminator(String lt) {
		this.lineTerminator = lt;
	}

	@Deprecated
	public String getLineTerminator() {
		return this.lineTerminator;
	}

	@Override
	synchronized public boolean openPort(String name, int baud) throws Exception {
		// this.inputBuffer = new StringBuilder();

		this.serialPort = new SerialPort(name);
		this.serialPort.openPort();
		this.serialPort.setParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, false, false);
		// this.serialPort.setParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
		// SerialPort.PARITY_NONE, true, true);
		this.serialPort.addEventListener(this);

		if (this.serialPort == null) {
			throw new Exception("Serial port not found.");
		}

		return serialPort.isOpened();
	}

	@Override
	public void closePort() throws Exception {
		if (this.serialPort != null) {
			try {
				this.serialPort.removeEventListener();

				if (this.serialPort.isOpened()) {
					this.serialPort.closePort();
				}
			} finally {
				this.inputBuffer = null;
				this.serialPort = null;
			}
		}
	}

	@Override
	public boolean isOpen() {
		return serialPort != null && serialPort.isOpened();
	}

	/**
	 * Sends a command to the serial device. This actually streams the bits to the comm port.
	 * 
	 * @param command
	 *            Command to be sent to serial device.
	 */
	@Override
	public void sendStringToComm(String command) throws Exception {
		this.serialPort.writeString(command);
	}

	/**
	 * Immediately sends a byte, used for real-time commands.
	 */
	@Override
	public void sendByteImmediately(byte b) throws Exception {
		this.serialPort.writeByte(b);
	}

	/**
	 * Reads data from the serial port. RXTX SerialPortEventListener method.
	 */
	@Override
	public void serialEvent(SerialPortEvent evt) {
		/*if (inputBuffer == null) {
			inputBuffer = new StringBuilder();
		}*/
		try {
			byte[] buf = this.serialPort.readBytes();
			if (buf != null && buf.length > 0) {

				LOGGER.debug("当前串口状态为:[{}]", evt.getEventType());

				String retValue = new String(buf, 0, buf.length);
				// 当开启示教模式时
				if (useReceivedMode) {
					if (startReceivingMode) { // 当开始演示时,将示教模式返回的gcode保存起来
						if (null == receivedGcodes) {
							receivedGcodes = new ArrayList<String>(2000);
						}
						LOGGER.debug("当前返回示教指令:[{}]", retValue);
						receivedGcodes.add(retValue);
					}
				} else {
					// 否则则将gcode执行的结果存入阻塞队列,以按顺序发送
					LOGGER.debug("串口返回16位字节转换后为:[{}]", new String(HexBytesUtil.encodeHex(buf)));
					queue.put(new SerialPortEventHolder(evt, buf));
				}

				/*inputBuffer.append(retValue);
				// Check for line terminator and split out command(s).
				if (inputBuffer.toString().contains(comm.getLineTerminator())) {
					// Split with the -1 option will give an empty string at
					// the end if there is a terminator there as well.
					String[] commands = inputBuffer.toString().split(comm.getLineTerminator(), -1);
					for (int i = 0; i < commands.length; i++) {
						// Make sure this isn't the last command.
						if ((i + 1) < commands.length) {
							comm.responseMessage(commands[i]);
							// Append last command to input buffer because it didn't have a
							// terminator.
						} else {
							inputBuffer = new StringBuilder().append(commands[i]);
						}
					}
				}*/
			}
		} catch (Exception e) {
			throw new MassaBotException("发送Gcode时出现异常", e);
		}
	}

	public static boolean supports(String portname, int baud) {
		SerialPort serialPort = new SerialPort(portname);
		try {
			serialPort.openPort();
			serialPort.setParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, true, true);
			serialPort.closePort();
			return true;
		} catch (SerialPortException e) {
			return false;
		} finally {
			if (serialPort.isOpened()) {
				try {
					serialPort.closePort();
				} catch (SerialPortException e) {
					// noop
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.massbot.codesender.connection.Connection#getCallBack()
	 */
	@Override
	public BlockingQueue<SerialPortEventHolder> getCallBack() {
		return queue;
	}

	/* (non-Javadoc)
	 * @see com.massabot.codesender.connection.Connection#startDemonstrate()
	 */
	@Override
	public void startReceivingGcode() {
		this.useReceivedMode = true;
		this.startReceivingMode = true;
	}

	/* (non-Javadoc)
	 * @see com.massabot.codesender.connection.Connection#finishDemonstrate()
	 */
	@Override
	public void pauseReceivingGcode() {
		this.startReceivingMode = false;
	}

	/* (non-Javadoc)
	 * @see com.massabot.codesender.connection.Connection#clearDemoCache()
	 */
	@Override
	public void finishReceivingGcode() {
		this.useReceivedMode = false;
	}

	/* (non-Javadoc)
	 * @see com.massabot.codesender.connection.Connection#getDemonstrateGcodes()
	 */
	@Override
	public List<String> getReceivedGcodes() {
		Assert.notEmpty(receivedGcodes, "示教模式生成的Gcode不能为空");
		List<String> gcodes = receivedGcodes;
		receivedGcodes = null;
		return gcodes;
	}

}
