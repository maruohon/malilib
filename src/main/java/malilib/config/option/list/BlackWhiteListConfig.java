package malilib.config.option.list;

import javax.annotation.Nullable;

import malilib.config.option.BaseGenericConfig;
import malilib.config.value.BlackWhiteList;

public class BlackWhiteListConfig<TYPE> extends BaseGenericConfig<BlackWhiteList<TYPE>>
{
    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue,
                                @Nullable String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultValue, name, commentTranslationKey, commentArgs);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue, String prettyName,
                                @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, name, prettyName, commentTranslationKey, commentArgs);
    }
}
