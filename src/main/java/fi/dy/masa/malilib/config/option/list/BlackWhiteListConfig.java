package fi.dy.masa.malilib.config.option.list;

import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;

public class BlackWhiteListConfig<TYPE> extends BaseGenericConfig<BlackWhiteList<TYPE>>
{
    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue,
                                String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultValue, name, commentTranslationKey, commentArgs);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue, String prettyName,
                                String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, name, prettyName, commentTranslationKey, commentArgs);
    }
}
