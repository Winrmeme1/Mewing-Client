/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.utils.discord;

import net.mewing.MewingClient;
import net.mewing.gui.screens.*;
import net.mewing.gui.screens.alts.AddAltScreen;
import net.mewing.gui.screens.alts.AltScreen;
import net.mewing.gui.screens.alts.EditAltScreen;
import net.mewing.gui.screens.proxy.AddProxyScreen;
import net.mewing.gui.screens.proxy.EditProxyScreen;
import net.mewing.gui.screens.proxy.ProxyScreen;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;

import static net.mewing.MewingClient.MC;

public class RPCManager {
    public static boolean started;
    private static final Discord rpc = Discord.INSTANCE;
    public static DiscordRPC presence = new DiscordRPC();
    private static Thread thread;

    public void startRpc() {
        if (!started) {
            started = true;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize("1268367396134191136", handlers, true, "");
            presence.startTimestamp = (System.currentTimeMillis() / 1000L);
            presence.largeImageText = "v" + MewingClient.MEWING_VERSION;
            rpc.Discord_UpdatePresence(presence);

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();

                    presence.details = getDetails();

                    presence.state = "v" + MewingClient.MEWING_VERSION + " | MC 1.21";

                    presence.smallImageText = "logged as - " + MC.getUser().getName();
                    presence.smallImageKey = "https://minotar.net/helm/" + MC.getUser().getName() + "/100.png";


                    presence.button_label_1 = "Download";
                    presence.button_url_1 = "https://github.com/coltonk9043/Mewing-MC-Hacked-Client";

                    presence.largeImageKey = "https://i.imgur.com/D64DjG2.png";

                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }, "TH-RPC-Handler");
            thread.start();
        }
    }

    /*
     Stops the Discord Rich Presence (RPC).
     */
    public void stopRpc() {
        if (started) {
            started = false;

            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }

            rpc.Discord_Shutdown();

            thread = null;
        }
    }

    private String getDetails() {
        String result = "";

        if (MC.screen instanceof TitleScreen || MC.screen instanceof MainMenuScreen) {
            result = "In Main menu";
        } else if (MC.screen instanceof JoinMultiplayerScreen || MC.screen instanceof ManageServerScreen) {
            result = "Picking a server";
        } else if (MC.getCurrentServer() != null) {
            result = "Playing on a server";
        } else if (MC.isLocalServer()) {
            result = "Playing singleplayer";
        } else if (MC.screen instanceof OptionsScreen) {
            result = "Editing options";
        } else if (MC.screen instanceof SelectWorldScreen) {
            result = "Selecting a world";
        } else if (MC.screen instanceof MewingCreditsScreen) {
            result = "Watching Mewing credits";
        } else if (MC.screen instanceof WinScreen) {
            result = "Watching credits";
        } else if (MC.screen instanceof CreateWorldScreen) {
            result = "Creating a world";
        } else if (MC.screen instanceof EditWorldScreen) {
            result = "Editing a world";
        } else if (MC.screen instanceof ProxyScreen) {
            result = "Choosing a proxy";
        } else if (MC.screen instanceof AltScreen) {
            result = "Choosing an alt";
        } else if (MC.screen instanceof AddProxyScreen) {
            result = "Creating a proxy";
        } else if (MC.screen instanceof EditProxyScreen) {
            result = "Editing a proxy";
        } else if (MC.screen instanceof AddAltScreen) {
            result = "Creating an alt";
        } else if (MC.screen instanceof EditAltScreen) {
            result = "Editing an alt";
        }
        return result;
    }
}
