package fi.dy.masa.malilib.config.options;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public interface IConfigOptionList
{
    IConfigOptionListEntry getOptionListValue();

    IConfigOptionListEntry getDefaultOptionListValue();

    void setOptionListValue(IConfigOptionListEntry value);
}
