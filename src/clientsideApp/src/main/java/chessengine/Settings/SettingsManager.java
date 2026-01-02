package chessengine.Settings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SettingsManager {

    private final Map<String, List<Option>> options;

    public SettingsManager() {
        options = new HashMap<>();
    }

    private List<OptionConfig> loadOptions(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(getClass().getResourceAsStream(path), new TypeReference<List<OptionConfig>>() {});
    };

    public void load(String resourcePath, String name){
        try{
            List<OptionConfig> configs = loadOptions(resourcePath);
            options.put(name, configs.stream().map(OptionConfig::getAsOption).collect(Collectors.toList()));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public List<Option> getOptions(String name) {
        return options.get(name);
    }

    public void populateSettingsWrapper(String name, Pane settingWrapper){
        List<Option> options = getOptions(name);

        // adding children in order that they populated the config
        for(Option option : options){
            settingWrapper.getChildren().add(option.getOptionNode());
        }
    }
}






