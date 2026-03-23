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
import net.mewing.settings.friends.Friend;
import net.mewing.settings.friends.FriendsList;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

public class CmdFriends extends Command {

    public CmdFriends() {
        super("friends", "Allows the player to add and remove friends (Who will be excluded from many hacks)", "[add/remove/list] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        FriendsList friendsList = Mewing.getInstance().friendsList;
        Minecraft MC = Minecraft.getInstance();
        switch (parameters[0]) {
            case "add": {
                String playerName = parameters[1];
                try {
                    ServerPlayer entity = MC.getSingleplayerServer().getPlayerList().getPlayerByName(playerName);
                    if (entity != null) {
                        Mewing.getInstance().friendsList.addFriend(entity.getName().getString(), entity.getUUID());
                        CommandManager.sendChatMessage("Player " + playerName + " was added to the friends list.");
                    } else {
                        CommandManager.sendChatMessage("Player " + playerName + " could not be found.");
                    }
                } catch (Exception e) {
                    CommandManager.sendChatMessage("Player " + playerName + " could not be added. Unknown error occured.");
                    return;
                }
            }
            break;
            case "remove": {
                String playerName = parameters[1];
                ServerPlayer entity = MC.getSingleplayerServer().getPlayerList().getPlayerByName(playerName);
                if (entity != null) {
                    Mewing.getInstance().friendsList.removeFriend(entity.getUUID());
                    CommandManager.sendChatMessage("Player " + playerName + " was removed from the friends list.");
                } else {
                    CommandManager.sendChatMessage("Player " + playerName + " could not be found.");
                }
            }
            break;
            case "list":
                StringBuilder friends = new StringBuilder("Friends: ");
                for (Friend friend : friendsList.getFriends()) {
                    friends.append(friend.getUsername()).append(", ");
                }
                friends.substring(0, friends.length() - 2);
                CommandManager.sendChatMessage(friends.toString());
                break;
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            case "add":
                return mc.getSingleplayerServer().getPlayerNames();
            case "remove":
                return new String[]{"xray", "delete"};
            default:
                return new String[]{"add", "remove", "list"};
        }
    }
}
