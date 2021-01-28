package fi.dy.masa.malilib.config.category;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigOption;

public class BaseConfigOptionCategory implements ConfigOptionCategory
{
    protected final String name;
    protected final boolean saveToFile;
    protected final List<? extends ConfigOption<?>> configs;

    public BaseConfigOptionCategory(String name, boolean saveToFile, List<? extends ConfigOption<?>> configs)
    {
        this.name = name;
        this.saveToFile = saveToFile;
        this.configs = configs;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean shouldSaveToFile()
    {
        return this.saveToFile;
    }

    @Override
    public List<? extends ConfigOption<?>> getConfigOptions()
    {
        return this.configs;
    }

    /**
     * Creates a normal config category that is shown on the config screen
     * and saved to a config file normally.
     */
    public static BaseConfigOptionCategory normal(String name, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigOptionCategory(name, true, configs);
    }

    /**
     * Creates a config category that is not saved to a file.
     */
    public static BaseConfigOptionCategory nonSaved(String name, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigOptionCategory(name, false, configs);
    }
}
