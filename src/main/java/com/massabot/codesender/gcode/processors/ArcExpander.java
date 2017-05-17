/**
 * Expand an arc into smaller sections. You can configure the length of each
 * section, and whether it is expanded with a bunch of smaller arcs, or with
 * line segments.
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import com.massabot.codesender.gcode.GcodeParser;
import com.massabot.codesender.gcode.GcodePreprocessorUtils;
import com.massabot.codesender.gcode.GcodeState;
import com.massabot.codesender.gcode.GcodeParser.GcodeMeta;
import com.massabot.codesender.gcode.util.GcodeParserException;
import com.massabot.codesender.gcode.util.PlaneFormatter;
import com.massabot.codesender.i18n.Localization;
import com.massabot.codesender.types.PointSegment;

/**
 *
 * @author wwinder
 */
public class ArcExpander implements ICommandProcessor {

	final private boolean convertToLines;
	final private double length;
	final private DecimalFormat df;

	@Override
	public String getHelp() {
		return Localization.getString("sender.help.arcs") + "\n" + Localization.getString("sender.arcs.length") + ": " + df.format(length);
	}

	/**
	 * @param convertToLines
	 *            toggles if smaller lines or arcs are returned.
	 * @param length
	 *            the length of each smaller segment.
	 */
	public ArcExpander(boolean convertToLines, double length) {
		this.convertToLines = convertToLines;
		this.length = length;

		// Setup decimal formatter
		df = new DecimalFormat("#.#########");
	}

	@Override
	public List<String> processCommand(String command, GcodeState state) throws GcodeParserException {
		if (state.currentPoint == null)
			throw new GcodeParserException(Localization.getString("parser.processor.arc.start-error"));

		List<String> results = new ArrayList<>();

		List<GcodeMeta> commands = GcodeParser.processCommand(command, 0, state);

		// If this is not an arc, there is nothing to do.
		if (!hasArcCommand(commands)) {
			results.add(command);
			return results;
		}

		// Make sure there is just one command (the arc).
		// Note: This means "G17 G02 X5 Y5 R2" would be an error.
		if (commands.size() != 1) {
			throw new GcodeParserException(Localization.getString("parser.processor.arc.multiple-commands"));
		}

		GcodeMeta arcMeta = commands.get(0);
		PointSegment ps = arcMeta.point;
		Point3d start = state.currentPoint;
		Point3d end = arcMeta.point.point();

		List<Point3d> points = GcodePreprocessorUtils.generatePointsAlongArcBDring(start, end, ps.center(), ps.isClockwise(), ps.getRadius(), 0, length,
				new PlaneFormatter(ps.getPlaneState()));

		// That function returns the first and last points. Exclude the first
		// point because the previous gcode command ends there already.
		points.remove(0);

		if (convertToLines) {
			// Tack the speed onto the first line segment in case the arc also
			// changed the feed value.
			String feed = "F" + arcMeta.point.getSpeed();
			for (Point3d point : points) {
				results.add(GcodePreprocessorUtils.generateG1FromPoints(start, point, state.inAbsoluteMode, df) + feed);
				start = point;
				feed = "";
			}
		} else {
			// TODO: Generate arc segments.
			throw new UnsupportedOperationException("I have not implemented this.");
		}

		return results;
	}

	private static boolean hasArcCommand(List<GcodeMeta> commands) {
		if (commands == null)
			return false;
		for (GcodeMeta meta : commands) {
			if (meta.point != null && meta.point.isArc()) {
				return true;
			}
		}
		return false;
	}
}
