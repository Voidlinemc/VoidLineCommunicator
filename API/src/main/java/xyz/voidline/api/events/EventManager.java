package xyz.voidline.api.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class EventManager {
    private static final Map<Class<? extends Event>, List<IEventListener<? extends Event>>> listeners = new HashMap<>();

    public static <T extends Event> void register(Class<T> eventClass, IEventListener<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    public static <T extends Event> void call(T event) {
        List<IEventListener<? extends Event>> list = listeners.get(event.getClass());
        if (list != null) {
            for (IEventListener<? extends Event> listener : list) {
                //noinspection unchecked
                ((IEventListener<T>) listener).onEvent(event);
            }
        }
    }
}