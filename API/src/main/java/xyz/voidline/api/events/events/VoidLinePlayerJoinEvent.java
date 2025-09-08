package xyz.voidline.api.events.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.voidline.api.IVoidLinePlayer;
import xyz.voidline.api.events.Event;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class VoidLinePlayerJoinEvent extends Event {
    private UUID uuid;
    private IVoidLinePlayer voidLinePlayer;
}
