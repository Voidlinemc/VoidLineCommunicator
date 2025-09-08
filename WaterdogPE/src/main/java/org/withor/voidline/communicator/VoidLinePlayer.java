package org.withor.voidline.communicator;

import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import org.cloudburstmc.protocol.bedrock.packet.ScriptMessagePacket;
import xyz.voidline.api.TabList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import xyz.voidline.api.IVoidLinePlayer;

public class VoidLinePlayer implements IVoidLinePlayer {
    private final ProxiedPlayer handle;

    public VoidLinePlayer(ProxiedPlayer player) {
        this.handle = player;
    }

    @Override
    public Object getHandle() {
        return handle;
    }

    @Override
    public void blockModule(String module) {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("module", module);

        String json = gson.toJson(obj);

        ScriptMessagePacket pkt = new ScriptMessagePacket();
        pkt.setChannel("voidline:blockmodule");
        pkt.setMessage(json);
        handle.sendPacket(pkt);
    }

    @Override
    public void unblockAll() {
        ScriptMessagePacket pkt = new ScriptMessagePacket();
        pkt.setChannel("voidline:unblockall");
        pkt.setMessage("");
        handle.sendPacket(pkt);
    }

    @Override
    public void setTabList(TabList tabList) {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("header", tabList.getHeader());
        obj.addProperty("footer", tabList.getFooter());

        String json = gson.toJson(obj);

        ScriptMessagePacket pkt = new ScriptMessagePacket();
        pkt.setChannel("voidline:tablist");
        pkt.setMessage(json);
        handle.sendPacket(pkt);
    }
}