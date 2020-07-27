package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;

public interface IConfigOptionList<T extends IConfigOptionListEntry<T>>
{
    T getOptionListValue();

    T getDefaultOptionListValue();

    void setOptionListValue(T value);

    default void cycleValue(boolean forward)
    {
        this.setOptionListValue(this.getOptionListValue().cycle(forward));
    }
}
