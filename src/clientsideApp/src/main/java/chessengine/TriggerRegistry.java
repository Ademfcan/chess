package chessengine;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TriggerRegistry {
    private static final Set<Trigger<?>> triggers = new HashSet<>();

    public static void registerTrigger(Trigger<?> trigger) {
        triggers.add(trigger);
    }

    public static void addTriggerable(Object triggerable) {
        if(triggers.isEmpty()){
            throw new RuntimeException("Trigger registry is empty! Are you calling before any triggers are registered?");
        }

        for (Trigger<?> trigger : triggers) {
            addIfMatches(trigger, triggerable);
        }
    }

    private static <T> void addIfMatches(Trigger<T> trigger, Object triggerable) {
        if (trigger.requiredClass.isAssignableFrom(triggerable.getClass())) {
            trigger.ifMatch.accept(trigger.requiredClass.cast(triggerable));
        }
    }

    public record Trigger<T>(Class<T> requiredClass, Consumer<T> ifMatch) {}
}
