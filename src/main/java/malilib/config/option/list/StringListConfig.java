package malilib.config.option.list;

import com.google.common.collect.ImmutableList;

public class StringListConfig extends ValueListConfig<String>
{
    public StringListConfig(String name, ImmutableList<String> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringListConfig(String name, ImmutableList<String> defaultValue,
                            String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, (v) -> v, (s) -> s, commentTranslationKey, commentArgs);
    }

    @Override
    public StringListConfig copy()
    {
        StringListConfig config = new StringListConfig(this.name, this.defaultValue);
        config.copyValuesFrom(this);
        return config;
    }
}
