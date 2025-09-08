package org.withor.voidline.plugin;

import dev.waterdog.waterdogpe.plugin.Plugin;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.net.URISyntaxException;

public class PluginMain extends Plugin {

    @Override
    public void onEnable() {
        try {
            boolean attachAllowed = Boolean.getBoolean("jdk.attach.allowAttachSelf");
            if (!attachAllowed) {
                getLogger().error("Self-attach is disabled use -Djdk.attach.allowAttachSelf=true");
                System.exit(1);
            }

            File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            String jarPath = jarFile.getAbsolutePath();
            String pid = ProcessHandle.current().pid() + "";

            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarPath);
            vm.detach();

            getLogger().info("Agent loaded!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}