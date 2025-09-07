package org.withor.voidline.communicator;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ScriptMessagePacket;
import cn.nukkit.utils.Config;
import org.withor.voidline.api.IVoidLinePlayer;
import org.withor.voidline.api.VoidLinePlayerStorage;

public class EventListener implements Listener {
    @EventHandler
    public void onPacketReceived(DataPacketReceiveEvent event) {
        if (!(event.getPacket() instanceof ScriptMessagePacket pkt)) {
            return;
        }

        if ("voidline:handshake".equals(pkt.getChannel())) {
            Player p = event.getPlayer();
            VoidLinePlayer vp = new VoidLinePlayer(p);
            VoidLinePlayerStorage.addPlayer(p.getUniqueId(), vp);

            Config config = VoidLineCommunicator.getCommunicator().getConfig();

            IVoidLinePlayer vlp = VoidLinePlayerStorage.getPlayer(p.getUniqueId());

            if(config.getBoolean("unblockAllModulesOnJoin")) vlp.unblockAll();

            @SuppressWarnings("unchecked")
            java.util.List<String> blocks = (java.util.List<String>) config.get("block");
            for(String module : blocks) {
                vlp.blockModule(module);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        VoidLinePlayerStorage.removePlayer(event.getPlayer().getUniqueId());
    }
}
