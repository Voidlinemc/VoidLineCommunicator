package xyz.voidline.api.events;

public interface IEventListener<T extends Event> {
    void onEvent(T event);
}