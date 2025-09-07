package org.withor.voidline.communicator.mixins;

import dev.waterdog.waterdogpe.network.protocol.rewrite.EntityMap;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.ScriptMessagePacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.withor.agent.Agent;
import org.withor.mixins.*;
import org.withor.voidline.api.IVoidLinePlayer;
import org.withor.voidline.api.VoidLinePlayerStorage;
import org.withor.voidline.communicator.VoidLinePlayer;

@Mixin(EntityMap.class)
public class EntityMapMixin {

    @Inject(method = "doRewrite", at = @At("HEAD"), cancellable = true)
    public static void onDoRewrite(EntityMap self, BedrockPacket packet, CallbackInfo<PacketSignal> ci) {
        ProxiedPlayer player = (ProxiedPlayer) ReflectionUtil.getPrivateField(self, "player");

        if (!(packet instanceof ScriptMessagePacket pkt)) {
            return;
        }

        if ("voidline:handshake".equals(pkt.getChannel())) {
            VoidLinePlayer vp = new VoidLinePlayer(player);
            VoidLinePlayerStorage.addPlayer(player.getUniqueId(), vp);

            IVoidLinePlayer vlp = VoidLinePlayerStorage.getPlayer(player.getUniqueId());

            vlp.unblockAll();

            @SuppressWarnings("unchecked")
            java.util.List<String> blocks = (java.util.List<String>) Agent.getConfig().get("block");
            for(String module : blocks) {
                vlp.blockModule(module);
            }
        }
    }
}