/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.command.commands;

import net.mewing.Mewing;
import net.mewing.command.Command;
import net.mewing.managers.CommandManager;
import net.mewing.command.InvalidSyntaxException;
import net.mewing.module.modules.movement.Step;

public class CmdStep extends Command {

	public CmdStep() {
		super("step", "Allows the player to step up blocks", "[toggle/height] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		Step module = Mewing.getInstance().moduleManager.step;

		switch (parameters[0]) {
		case "height":
			try {
				float height = Float.parseFloat(parameters[1]);
				module.setStepHeight(height);
				CommandManager.sendChatMessage("Step height set to " + height);

			} catch (Exception e) {
				CommandManager.sendChatMessage("Invalid value.");
			}
			break;
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("Step toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("Step toggled OFF");
			} else {
				CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
			}
			break;
		default:
			throw new InvalidSyntaxException(this);
		}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "toggle":
			return new String[] { "on", "off" };
		case "height":
			return new String[] { "0.5", "1.0", "1.5", "2.0", "5.0", "10.0" };
		default:
			return new String[] { "toggle", "height" };
		}
	}
}
