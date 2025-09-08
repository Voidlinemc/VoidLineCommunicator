package org.withor.voidline.communicator;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.ScriptMessagePacket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import xyz.voidline.api.IVoidLinePlayer;
import xyz.voidline.api.TabList;

public class VoidLinePlayer implements IVoidLinePlayer {
    private final Player handle;

    public VoidLinePlayer(Player player) {
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

        ScriptMessagePacket pkt = new ScriptMessagePacket("voidline:blockmodule", json);
        handle.dataPacket(pkt);
    }

    @Override
    public void unblockAll() {
        ScriptMessagePacket pkt = new ScriptMessagePacket("voidline:unblockall", "");
        handle.dataPacket(pkt);
    }
    @Override
    public void setTabList(TabList tabList) {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("header", tabList.getHeader());
        obj.addProperty("footer", tabList.getFooter());

        String json = gson.toJson(obj);

        ScriptMessagePacket pkt = new ScriptMessagePacket("voidline:tablist", json);
        handle.dataPacket(pkt);
    }
}