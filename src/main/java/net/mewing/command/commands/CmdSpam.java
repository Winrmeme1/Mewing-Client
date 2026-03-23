/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.command.commands;

import net.mewing.command.Command;
import net.mewing.command.InvalidSyntaxException;

public class CmdSpam extends Command {

    public CmdSpam() {
        super("spam", "Spams the chat with a certain message.", "[times] [message]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length < 2)
            throw new InvalidSyntaxException(this);

        // Combines the "parameters" into a string to be printed.
        StringBuilder message = new StringBuilder();
        for (int msg = 1; msg < parameters.length; msg++) {
            message.append(parameters[msg]).append(" ");
        }

        // Prints out that message X number of times.
        for (int i = 0; i < Integer.parseInt(parameters[0]); i++) {
            mc.player.connection.sendChat(message.toString());
        }

    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            default:
                return new String[]{"Mewing is an amazing client!"};
        }
    }
}
