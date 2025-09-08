package org.withor.voidline.communicator;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import xyz.voidline.api.IVoidLinePlayer;
import org.withor.voidline.protocol.ScriptMessagePacket;
import cn.nukkit.utils.Config;
import xyz.voidline.api.VoidLinePlayerStorage;
import xyz.voidline.api.events.EventManager;
import xyz.voidline.api.events.events.VoidLinePlayerJoinEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onPacketReceived(DataPacketReceiveEvent event) {
        if (!(event.getPacket() instanceof ScriptMessagePacket pkt)) {
            return;
        }

        if ("voidline:handshake".equals(pkt.getChannel())) {
            Player player = event.getPlayer();
            VoidLinePlayer vp = new VoidLinePlayer(player);
            VoidLinePlayerStorage.addPlayer(player.getUniqueId(), vp);

            Config config = VoidLineCommunicator.getCommunicator().getConfig();

            IVoidLinePlayer vlp = VoidLinePlayerStorage.getPlayer(player.getUniqueId());

            if(config.getBoolean("unblockAllModulesOnJoin")) vlp.unblockAll();

            @SuppressWarnings("unchecked")
            java.util.List<String> blocks = (java.util.List<String>) config.get("block");
            for(String module : blocks) {
                vlp.blockModule(module);
            }

            EventManager.call(new VoidLinePlayerJoinEvent(player.getUniqueId(), vp));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        VoidLinePlayerStorage.removePlayer(event.getPlayer().getUniqueId());
    }
}
