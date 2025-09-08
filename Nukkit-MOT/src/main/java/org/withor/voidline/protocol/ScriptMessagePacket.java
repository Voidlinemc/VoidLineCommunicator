package org.withor.voidline.protocol;

import cn.nukkit.network.protocol.DataPacket;

public class ScriptMessagePacket extends DataPacket {
    @Override
    public byte pid() {
        return (byte) 177;
    }

    private String channel;
    private String message;

    public ScriptMessagePacket() {}
    public ScriptMessagePacket(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    @Override
    public void decode() {
        this.channel = this.getString();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(channel);
        this.putString(message);
    }

    public String getChannell() {
        return channel;
    }

    public String getMessage() {
        return message;
    }
}
