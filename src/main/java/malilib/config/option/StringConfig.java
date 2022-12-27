package malilib.config.option;

import javax.annotation.Nullable;

public class StringConfig extends BaseGenericConfig<String>
{
    public StringConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringConfig(String name, String defaultValue,
                        @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, commentTranslationKey, commentArgs);
    }
}
