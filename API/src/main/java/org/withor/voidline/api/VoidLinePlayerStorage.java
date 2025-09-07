package org.withor.voidline.api;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoidLinePlayerStorage {
    private static final Map<UUID, IVoidLinePlayer> PLAYERS = new ConcurrentHashMap<>();

    public static void addPlayer(UUID uuid, IVoidLinePlayer player) {
        PLAYERS.put(uuid, player);
    }

    public static void removePlayer(UUID uuid) {
        PLAYERS.remove(uuid);
    }

    public static IVoidLinePlayer getPlayer(UUID uuid) {
        return PLAYERS.get(uuid);
    }
}