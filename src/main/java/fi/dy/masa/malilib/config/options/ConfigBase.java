package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigValue;

public abstract class ConfigBase implements IConfigValue
{
    private final ConfigType type;
    private final String name;
    private String comment;

    public ConfigBase(ConfigType type, String name, String comment)
    {
        this.type = type;
        this.name = name;
        this.comment = comment;
    }

    @Override
    public ConfigType getType()
    {
        return this.type;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    @Nullable
    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
