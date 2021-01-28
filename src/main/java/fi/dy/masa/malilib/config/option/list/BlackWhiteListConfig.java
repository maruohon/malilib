package fi.dy.masa.malilib.config.option.list;

import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;

public class BlackWhiteListConfig<TYPE> extends BaseGenericConfig<BlackWhiteList<TYPE>>
{
    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue, String prettyName, String comment)
    {
        super(name, defaultValue, name, prettyName, comment);
    }
}
