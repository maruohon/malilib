package fi.dy.masa.malilib.config.category;

import java.util.List;
import fi.dy.masa.malilib.config.ConfigDeserializer;
import fi.dy.masa.malilib.config.ConfigOption;
import fi.dy.masa.malilib.config.ConfigSerializer;

public class BaseConfigOptionCategory implements ConfigOptionCategory
{
    protected final String name;
    protected final boolean saveToFile;
    protected final List<? extends ConfigOption<?>> configs;
    protected ConfigSerializer serializer = ConfigOption::getAsJsonElement;
    protected ConfigDeserializer deSerializer = ConfigOption::setValueFromJsonElement;

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

    @Override
    public ConfigDeserializer getDeserializer()
    {
        return this.deSerializer;
    }

    @Override
    public ConfigSerializer getSerializer()
    {
        return this.serializer;
    }

    public BaseConfigOptionCategory setSerializer(ConfigSerializer serializer)
    {
        this.serializer = serializer;
        return this;
    }

    public BaseConfigOptionCategory setDeSerializer(ConfigDeserializer deSerializer)
    {
        this.deSerializer = deSerializer;
        return this;
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
