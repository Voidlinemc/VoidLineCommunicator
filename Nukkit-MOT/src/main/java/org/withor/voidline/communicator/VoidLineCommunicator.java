package org.withor.voidline.communicator;

import cn.nukkit.network.protocol.ProtocolInfo;
import org.withor.voidline.protocol.ScriptMessagePacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class VoidLineCommunicator extends PluginBase {
    @Getter
    private Config config;
    @Getter
    private static VoidLineCommunicator communicator;

    @Override
    public void onEnable() {
        this.getServer().getNetwork().registerPacket(ProtocolInfo.v1_21_70, (byte) 177, ScriptMessagePacket.class);
        communicator = this;
        config = new Config(
                new File(this.getDataFolder(), "config.yml"),
                Config.YAML,
                new ConfigSection(new LinkedHashMap<>() {{
                    put("block", Arrays.asList());
                    put("unblockAllModulesOnJoin", true);
                }})
        );

        config.save();
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
    }
}
