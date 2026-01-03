package chessengine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerManager {
    private static final Map<Class<?>, List<Object>> triggerables = new HashMap<>();


    public static <T> void registerAccumulatingTrigger(Class<T> requiredClass) {
        // Notify all triggers that match this triggerable
        TriggerRegistry.registerTrigger(new TriggerRegistry.Trigger<>(requiredClass, triggerable -> {
            triggerables.putIfAbsent(requiredClass, new java.util.ArrayList<>());
            triggerables.get(requiredClass).add(triggerable);
        }));
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getTriggerables(Class<T> requiredClass) {
        return (List<T>) triggerables.getOrDefault(requiredClass, List.of());
    }
}
