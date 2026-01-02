package chessengine.Settings;

import java.util.List;

public class OptionConfig {
    public String name;
    public String description;
    public Object defaultValue;
    public Object options;

    public Option getAsOption() {
        if (options instanceof List){
            // could either be string or numeric
            List<?> optionsList = ((List<?>)options);

            if(optionsList.isEmpty()){
                throw new IllegalArgumentException("options list must be non-empty!");
            }

            return new ListOption<>(name, description, defaultValue, optionsList);
        }

        if (options instanceof String){
            // first case Range defined as "a-b"
            String optionsStr = (String) options;

            if(optionsStr.contains("-")){
                int index = optionsStr.indexOf("-");
                double min = Double.parseDouble(optionsStr.substring(0, index));
                double max = Double.parseDouble(optionsStr.substring(index+1));
                double defaultValue = Double.parseDouble(this.defaultValue.toString());

                return new RangeOption(name, description, defaultValue, min, max);
            }

        }

        if(options == null){
            return new BooleanOption(name, description, (boolean) defaultValue);
        }

        throw new IllegalArgumentException("Unknown option type for: " + name + " " + options.toString());
    }


}
