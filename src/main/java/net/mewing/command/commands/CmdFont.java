/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.command.commands;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.command.Command;
import net.mewing.managers.CommandManager;
import net.minecraft.client.gui.Font;
import net.mewing.command.InvalidSyntaxException;

public class CmdFont extends Command {

    public CmdFont() {
        super("font", "Sets the HUD font.", "[set] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length != 2)
            throw new InvalidSyntaxException(this);

        MewingClient mewing = Mewing.getInstance();

        switch (parameters[0]) {
            case "set":
                try {
                    String font = parameters[1];
                    Font t = mewing.fontManager.fontRenderers.get(font);
                    if (t != null) {
                        mewing.fontManager.SetRenderer(t);
                    }
                } catch (Exception e) {
                    CommandManager.sendChatMessage("Invalid value.");
                }
                break;
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            case "set":
                MewingClient mewing = Mewing.getInstance();

                String[] suggestions = new String[mewing.fontManager.fontRenderers.size()];

                int i = 0;
                for (String fontName : mewing.fontManager.fontRenderers.keySet())
                    suggestions[i++] = fontName;

                return suggestions;
            default:
                return new String[]{"set"};
        }
    }
}