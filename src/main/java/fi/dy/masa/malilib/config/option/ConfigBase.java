package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class ConfigBase<T> implements IConfigBase, IConfigResettable, IConfigNotifiable<T>
{
    private final ConfigType type;
    private final String name;
    private final String prettyName;
    private String comment;
    private String modName = "";
    @Nullable
    private IValueChangeCallback<T> callback;

    public ConfigBase(ConfigType type, String name, String comment)
    {
        this(type, name, comment, name);
    }

    public ConfigBase(ConfigType type, String name, String comment, String prettyName)
    {
        this.type = type;
        this.name = name;
        this.prettyName = prettyName;
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
    public String getPrettyName()
    {
        return StringUtils.translate(this.prettyName);
    }

    @Override
    @Nullable
    public String getComment()
    {
        return StringUtils.translate(this.comment);
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public String getModName()
    {
        return this.modName;
    }

    @Override
    public void setModName(String modName)
    {
        this.modName = modName;
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback<T> callback)
    {
        this.callback = callback;
    }

    @Override
    public void onValueChanged(T newValue, T oldValue)
    {
        if (this.callback != null)
        {
            this.callback.onValueChanged(newValue, oldValue);
        }
    }
}
