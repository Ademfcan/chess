package chessengine.Settings;

import chessengine.App;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.FXInitQueue;
import chessserver.Misc.OptionMarker;
import chessserver.User.UserPreferences;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;


public class SettingsManager {

    private static final Class<?> optionsClass = UserPreferences.class;

    private static boolean optionsLoaded = false;
    private static List<ListOption<?>> options;

    private static final Queue<VBox> settingWrappers = new LinkedList<>();
    private static final Set<VBox> loadedSettingWrappers = new HashSet<>();


    static{
        FXInitQueue.runAfterInit(() -> {
            loadOptions(PersistentSaveManager.userPreferenceTracker.getTracked()); // initial user preferences
            flushWrappers();
        });
    }

    private static void loadOptions(UserPreferences userPreferences) {
        // go over user preferences fields, and create an option for each
        options = new LinkedList<>();
        optionsLoaded = true;

        try{
            for(Field field : optionsClass.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(OptionMarker.class)) {
                    if (!(Enum.class.isAssignableFrom(field.getType()))) {
                        throw new IllegalArgumentException("OptionMarker can only be used on enum fields");
                    }

                    // Cast the Class to a generic enum class
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) field.getType();

                    // Get the enum value (unchecked cast)
                    @SuppressWarnings("unchecked")
                    Enum<?> enumValue = (Enum<?>) field.get(userPreferences);
                    OptionMarker marker = field.getAnnotation(OptionMarker.class);



                    options.add(loadOptionGeneric(enumClass, enumValue, marker, (onChange) -> {
                        try {
                            // Set the new value back to the field
                            field.set(PersistentSaveManager.userPreferenceTracker.getTracked(), onChange);
                            App.triggerUpdateUser();

                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Failed to set field %s in %s class".formatted(field.getName(), optionsClass.getSimpleName()), e);
                        }
                    }));

                }

            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access fields in %s class".formatted(optionsClass.getSimpleName()), e);
        }
    }

    private static <T extends Enum<T>> ListOption<T> loadOptionGeneric(Class<? extends Enum<?>> enumClass, Enum<?> enumValue, OptionMarker marker, Consumer<T> onChange) {
        // Cast enumValue to T

        @SuppressWarnings("unchecked")
        T typedEnumValue = (T) enumClass.cast(enumValue);

        return loadOption(typedEnumValue, marker, onChange);
    }

    private static <T extends Enum<T>> ListOption<T> loadOption(T enumValue, OptionMarker marker, Consumer<T> onChange) {
        return new ListOption<>(marker.displayName(), marker.description(), enumValue,
                Arrays.stream(enumValue.getDeclaringClass().getEnumConstants()).toList(), onChange);
    }



    public static void addSettingsWrapper(VBox settingWrapper){
        Objects.requireNonNull(settingWrapper);

        if(!optionsLoaded){
            // to be loaded later
            settingWrappers.offer(settingWrapper);
        }
        else{
            // options already loaded, so we can load the settings wrapper immediately
            loadSettingsWrapper(settingWrapper);
        }
    }

    private static void flushWrappers(){
        if(options == null){
            throw new IllegalStateException("Option configs not loaded yet!");
        }

        // for each setting wrapper, add each option to it
        for(VBox settingWrapper : settingWrappers){
            loadSettingsWrapper(settingWrapper);
        }

    }


    private static void loadSettingsWrapper(VBox settingWrapper){
        // add each option to the setting wrapper
        // adding children in order that they populated the config
        settingWrapper.getChildren().clear();
        settingWrapper.setSpacing(10);

        for(Option option : options){
            settingWrapper.getChildren().add(option.getOptionNode(settingWrapper));
        }

        loadedSettingWrappers.add(settingWrapper);
    }


    public static void updateSettingsWrapper(UserPreferences newPreferences) {
        loadOptions(newPreferences);

        // clear the wrapper and reload it with new options
        for(VBox settingWrapper : loadedSettingWrappers){
            settingWrapper.getChildren().clear();
            loadSettingsWrapper(settingWrapper);
        }
    }
}






