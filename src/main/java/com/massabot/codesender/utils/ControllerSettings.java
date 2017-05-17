/**
 * POJO Object representation of a controller JSON file.
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
package com.massabot.codesender.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.massabot.codesender.AbstractController;
import com.massabot.codesender.GrblController;
import com.massabot.codesender.LoopBackCommunicator;
import com.massabot.codesender.SmoothieController;
import com.massabot.codesender.TinyGController;
import com.massabot.codesender.XLCDCommunicator;
import com.massabot.codesender.gcode.processors.ICommandProcessor;
import com.massabot.codesender.gcode.util.CommandProcessorLoader;

/**
 *
 * @author wwinder
 */
public class ControllerSettings {

	private class ControllerConfig {

		public String name;
		public JsonElement args;
	}

	static public class ProcessorConfig {

		public String name;
		public Boolean enabled = true;
		public Boolean optional = true;
		public JsonObject args = null;

		public ProcessorConfig(String name, Boolean enabled, Boolean optional, JsonObject args) {
			this.name = name;
			this.enabled = enabled;
			this.optional = optional;
			this.args = args;
		}
	}

	public class ProcessorConfigGroups {

		public ArrayList<ProcessorConfig> Front;
		public ArrayList<ProcessorConfig> Custom;
		public ArrayList<ProcessorConfig> End;
	}

	String Name;
	Integer Version = 0;
	ControllerConfig Controller;
	ProcessorConfigGroups GcodeProcessors;

	public enum CONTROLLER {
		GRBL("GRBL"), SMOOTHIE("SmoothieBoard"), TINYG("TinyG"), XLCD("XLCD"), LOOPBACK("Loopback"), LOOPBACK_SLOW("Loopback_Slow");

		final String name;

		CONTROLLER(String name) {
			this.name = name;
		}

		public static CONTROLLER fromString(String name) {
			for (CONTROLLER c : values()) {
				if (c.name.equalsIgnoreCase(name)) {
					return c;
				}
			}
			return null;
		}
	}

	public String getName() {
		return Name;
	}

	public Integer getVersion() {
		return Version;
	}

	/**
	 * Parse the "Controller" object in the firmware config json.
	 * 
	 * "Controller": { "name": "GRBL", "args": null }
	 */
	public AbstractController getController() {
		// String controllerName = controllerConfig.get("name").getAsString();
		String controllerName = this.Controller.name;
		CONTROLLER controller = CONTROLLER.fromString(controllerName);
		switch (controller) {
		case GRBL:
			return new GrblController();
		case SMOOTHIE:
			return new SmoothieController();
		case TINYG:
			return new TinyGController();
		case XLCD:
			return new GrblController(new XLCDCommunicator());
		case LOOPBACK:
			return new GrblController(new LoopBackCommunicator());
		case LOOPBACK_SLOW:
			return new GrblController(new LoopBackCommunicator(100));
		default:
			throw new AssertionError(controller.name());
		}
	}

	/**
	 * Get the list of processors from the settings in the order they should be applied.
	 */
	// TODO: Remove settings
	public List<ICommandProcessor> getProcessors() {
		List<ICommandProcessor> ret = new ArrayList<>();
		ret.addAll(CommandProcessorLoader.initializeWithProcessors(GcodeProcessors.Front));
		ret.addAll(CommandProcessorLoader.initializeWithProcessors(GcodeProcessors.Custom));
		ret.addAll(CommandProcessorLoader.initializeWithProcessors(GcodeProcessors.End));
		return ret;
	}

	public ProcessorConfigGroups getProcessorConfigs() {
		return this.GcodeProcessors;
	}
}