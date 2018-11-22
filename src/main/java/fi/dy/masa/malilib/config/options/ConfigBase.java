package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigValueNotifiable;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;

public abstract class ConfigBase implements IConfigValueNotifiable
{
    private final ConfigType type;
    private final String name;
    private String comment;
    @Nullable
    private IValueChangeCallback callback;

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

    @Override
    public void setValueChangeCallback(IValueChangeCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public void onValueChanged()
    {
        if (this.callback != null)
        {
            this.callback.onValueChanged(this);
        }
    }
}
