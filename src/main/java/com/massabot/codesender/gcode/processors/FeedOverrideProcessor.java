/**
 * Overrides any F commands with the given percentage.
 */
/*
    Copywrite 2016 Will Winder

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
package com.massabot.codesender.gcode.processors;

import java.util.ArrayList;
import java.util.List;

import com.massabot.codesender.gcode.GcodePreprocessorUtils;
import com.massabot.codesender.gcode.GcodeState;
import com.massabot.codesender.i18n.Localization;

/**
 *
 * @author wwinder
 */
public class FeedOverrideProcessor implements ICommandProcessor {

	private final double percentOverride;

	public FeedOverrideProcessor(double percentOverride) {
		this.percentOverride = percentOverride;
	}

	@Override
	public String getHelp() {
		return Localization.getString("sender.help.speed.override") + "\n" + Localization.getString("sender.help.speed.percent") + "\n"
				+ Localization.getString("sender.speed.percent") + ": " + percentOverride;
	}

	@Override
	public List<String> processCommand(String command, GcodeState state) {
		List<String> ret = new ArrayList<>();
		if (percentOverride > 0) {
			ret.add(GcodePreprocessorUtils.overrideSpeed(command, percentOverride));
		} else {
			ret.add(command);
		}
		return ret;
	}
}