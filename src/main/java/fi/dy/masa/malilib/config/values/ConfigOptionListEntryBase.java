package fi.dy.masa.malilib.config.values;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigOptionListEntryBase  <T extends ConfigOptionListEntryBase<T>> implements IConfigOptionListEntry
{
    protected final List<T> values;
    protected final String configString;
    protected final String translationKey;
    protected int index;

    public ConfigOptionListEntryBase(String configString, String translationKey)
    {
        this.values = new ArrayList<>();
        this.configString = configString;
        this.translationKey = translationKey;
        this.index = -1;
    }

    public void init(List<T> values)
    {
        this.values.clear();

        for (int i = 0; i < values.size(); ++i)
        {
            T entry = values.get(i); 
            this.values.add(entry);

            if (entry == this)
            {
                this.index = i;
            }
        }
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public T cycle(boolean forward)
    {
        int index = this.index;
        int size = this.values.size();

        if (index < 0)
        {
            throw new RuntimeException(String.format("Entry %s (%s) not found on list of all values (size: %d)!", this.getDisplayName(), this.getStringValue(), this.values.size()));
        }

        if (forward)
        {
            if (++index >= size)
            {
                index = 0;
            }
        }
        else
        {
            if (--index < 0)
            {
                index = size - 1;
            }
        }

        return this.values.get(index % size);
    }

    @Override
    public T fromString(String name)
    {
        return fromStringStatic(this.values, name);
    }

    public List<T> getValues()
    {
        return this.values;
    }

    public static <T extends ConfigOptionListEntryBase<T>> T fromStringStatic(List<T> values, String name)
    {
        for (T val : values)
        {
            if (val.configString.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return values.get(0);
    }

    public static <T extends ConfigOptionListEntryBase<T>> void initValues(List<T> values)
    {
        for (T val : values)
        {
            val.init(values);
        }
    }
}
