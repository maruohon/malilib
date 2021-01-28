package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;

public class OptionListConfig<T extends ConfigOptionListEntry<T>> extends BaseGenericConfig<T>
{
    public OptionListConfig(String name, T defaultValue)
    {
        this(name, defaultValue, name);
    }

    public OptionListConfig(String name, T defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public OptionListConfig(String name, T defaultValue, String prettyName, String comment)
    {
        super(name, defaultValue, name, prettyName, comment);
    }

    public void cycleValue(boolean forward)
    {
        this.setValue(this.value.cycle(forward));
    }
}
