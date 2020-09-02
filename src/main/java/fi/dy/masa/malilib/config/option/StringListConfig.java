package fi.dy.masa.malilib.config.option;

import com.google.common.collect.ImmutableList;

public class StringListConfig extends ValueListConfig<String>
{
    public StringListConfig(String name, ImmutableList<String> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringListConfig(String name, ImmutableList<String> defaultValue, String comment)
    {
        super(name, defaultValue, comment, (v) -> v, (s) -> s);
    }
}
