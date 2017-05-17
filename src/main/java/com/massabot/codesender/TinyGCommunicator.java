/*
 * TinyG serial port interface class.
 */

/*
    Copywrite 2012-2015 Will Winder

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
package com.massabot.codesender;

import com.massabot.codesender.connection.Connection;
import com.massabot.codesender.types.TinyGGcodeCommand;

/**
 *
 * @author wwinder
 */
public class TinyGCommunicator extends BufferedCommunicator {

	public Connection getConnection() {
		return super.conn;
	}

	@Override
	public int getBufferSize() {
		return 127;
	}

	@Override
	public String getLineTerminator() {
		return "\n";
	}

	@Override
	protected boolean processedCommand(String response) {
		return TinyGGcodeCommand.isOkErrorResponse(response);
	}

	/**
	 * Allows detecting errors and pausing the stream.
	 */
	@Override
	protected boolean processedCommandIsError(String response) {
		return false;
	}

	@Override
	protected void sendingCommand(String response) {
		// no-op for this protocol.
	}
}