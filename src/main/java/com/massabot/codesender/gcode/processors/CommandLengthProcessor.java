/**
 * Throws an exception if the command is too long.
 */
package com.massabot.codesender.gcode.processors;

import java.util.ArrayList;
import java.util.List;

import com.massabot.codesender.gcode.GcodeState;
import com.massabot.codesender.gcode.util.GcodeParserException;
import com.massabot.codesender.i18n.Localization;

/**
 *
 * @author wwinder
 */
public class CommandLengthProcessor implements ICommandProcessor {

	final private int length;

	public CommandLengthProcessor(int length) {
		this.length = length;
	}

	@Override
	public String getHelp() {
		return Localization.getString("sender.help.command.length") + "\n" + Localization.getString("sender.command.length") + ": " + length;
	}

	@Override
	public List<String> processCommand(String command, GcodeState state) throws GcodeParserException {
		if (command.length() > length)
			throw new GcodeParserException("Command '" + command + "' is longer than " + length + " characters.");

		List<String> ret = new ArrayList<>();
		ret.add(command);
		return ret;
	}

}
