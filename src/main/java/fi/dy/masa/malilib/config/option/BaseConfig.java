package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IValueChangeCallback;
import fi.dy.masa.malilib.config.IValueLoadedCallback;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfig<T> implements ConfigOption<T>
{
    protected final ConfigType type;
    protected final String name;
    protected final String prettyNameTranslationKey;
    protected String commentTranslationKey;
    protected Object[] commentArgs = new Object[0];
    protected String modName = "";
    @Nullable
    protected IValueChangeCallback<T> valueChangeCallback;
    @Nullable
    protected IValueLoadedCallback<T> valueLoadCallback;

    public BaseConfig(ConfigType type, String name, String commentTranslationKey)
    {
        this(type, name, commentTranslationKey, name);
    }

    public BaseConfig(ConfigType type, String name, String commentTranslationKey, Object... commentArgs)
    {
        this(type, name, commentTranslationKey, name);

        this.commentArgs = commentArgs;
    }

    public BaseConfig(ConfigType type, String name, String commentTranslationKey, String prettyNameTranslationKey)
    {
        this.type = type;
        this.name = name;
        this.prettyNameTranslationKey = prettyNameTranslationKey;
        this.commentTranslationKey = commentTranslationKey;
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
        return StringUtils.translate(this.prettyNameTranslationKey);
    }

    @Override
    @Nullable
    public String getCommentTranslationKey()
    {
        return commentTranslationKey;
    }

    @Override
    @Nullable
    public String getComment()
    {
        return StringUtils.translate(this.getCommentTranslationKey(), this.commentArgs);
    }

    public BaseConfig<T> setCommentArgs(Object... args)
    {
        this.commentArgs = args;
        return this;
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
    public void setValueChangeCallback(@Nullable IValueChangeCallback<T> callback)
    {
        this.valueChangeCallback = callback;
    }

    @Override
    public void setValueLoadCallback(@Nullable IValueLoadedCallback<T> callback)
    {
        this.valueLoadCallback = callback;
    }

    @Override
    public void onValueChanged(T newValue, T oldValue)
    {
        if (this.valueChangeCallback != null)
        {
            this.valueChangeCallback.onValueChanged(newValue, oldValue);
        }
    }

    @Override
    public void onValueLoaded(T newValue)
    {
        if (this.valueLoadCallback != null)
        {
            this.valueLoadCallback.onValueLoaded(newValue);
        }
    }
}
