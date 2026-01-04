package chessengine;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AnnotationProcessor {
    private static final Set<AnnotationHandler> annotationHandlers = new HashSet<>();

    public static void addAnnotationHandler(AnnotationHandler handler) {
        annotationHandlers.add(handler);
    }

    public static void processAnnotations() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            List<Class<?>> classes = scanResult.getClassesWithAnnotation(AutoRegister.class.getName()).loadClasses();

            for(Class<?> clazz : classes){
                for (AnnotationHandler handler : annotationHandlers) {
                    if(handler.requiredClass.isAssignableFrom(clazz)){
                        handler.ifMatch().accept(clazz);
                    }
                }
            }
        }
    }


    public record AnnotationHandler(Class<?> requiredClass, Consumer<Class<?>> ifMatch){}
}
