package org.withor.voidline.communicator.mixins;

import dev.waterdog.waterdogpe.network.protocol.ProtocolCodecs;
import org.apache.logging.log4j.Logger;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.ScriptMessagePacket;
import org.withor.agent.Agent;
import org.withor.agent.AgentLogger;
import org.withor.mixins.*;

import java.util.List;

@Mixin(ProtocolCodecs.class)
public class ProtocolCodecsMixin {
    private static final Logger logger = AgentLogger.getLogger(ProtocolCodecsMixin.class);

    @Inject(method = "<clinit>", at = @At("TAIL"), cancellable = false)
    public static void onStaticInit(CallbackInfo<Void> ci) {
        try {
            @SuppressWarnings("unchecked")
            List<Class<? extends BedrockPacket>> packets = (List<Class<? extends BedrockPacket>>) ReflectionUtil.getPrivateField(null, "HANDLED_PACKETS", ProtocolCodecs.class);

            packets.add(ScriptMessagePacket.class);
            logger.info("patched packets registory");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}