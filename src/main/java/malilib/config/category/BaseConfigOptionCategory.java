package malilib.config.category;

import java.util.List;

import malilib.config.option.ConfigOption;
import malilib.util.data.ModInfo;

public class BaseConfigOptionCategory implements ConfigOptionCategory
{
    protected final ModInfo modInfo;
    protected final String name;
    protected final boolean saveToFile;
    protected final List<? extends ConfigOption<?>> configs;

    public BaseConfigOptionCategory(ModInfo modInfo,
                                    String name,
                                    boolean saveToFile,
                                    List<? extends ConfigOption<?>> configs)
    {
        this.modInfo = modInfo;
        this.name = name;
        this.saveToFile = saveToFile;
        this.configs = configs;
    }

    @Override
    public ModInfo getModInfo()
    {
        return this.modInfo;
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
    public static BaseConfigOptionCategory normal(ModInfo modInfo, String name, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigOptionCategory(modInfo, name, true, configs);
    }

    /**
     * Creates a config category that is not saved to a file.
     */
    public static BaseConfigOptionCategory nonSaved(ModInfo modInfo, String name, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigOptionCategory(modInfo, name, false, configs);
    }
}
