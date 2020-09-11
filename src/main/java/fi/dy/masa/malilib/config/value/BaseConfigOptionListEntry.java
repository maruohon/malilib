package fi.dy.masa.malilib.config.value;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseConfigOptionListEntry<T extends BaseConfigOptionListEntry<T>> implements ConfigOptionListEntry<T>
{
    protected final String configString;
    protected final String translationKey;
    protected ImmutableList<T> values;
    protected int index;

    public BaseConfigOptionListEntry(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
        this.index = -1;
    }

    public void init(ImmutableList<T> values)
    {
        this.values = values;
        this.index = values.indexOf(this);
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
        return cycleValue(this.values, this.index, forward);
    }

    @Override
    public T fromString(String name)
    {
        return findValueByName(name, this.values);
    }

    public List<T> getValues()
    {
        return this.values;
    }

    /**
     * Cycles the value either to the previous or the next value
     * from the provided list, based on the provided starting index
     * @param values
     * @param currentIndex
     * @param forward
     * @return
     */
    public static <T extends ConfigOptionListEntry<T>> T cycleValue(List<T> values, int currentIndex, boolean forward)
    {
        final int size = values.size();
        int index = currentIndex;

        if (index < 0)
        {
            index = 0;
            MaLiLib.LOGGER.warn("Invalid index {} when trying to cycle a config option list value", index);
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

        return values.get(index % size);
    }

    /**
     * 
     * Finds the value by the given name from the provided list.
     * If none of the entries match, then the first entry is returned as a fall-back.
     * @param name
     * @param values
     * @return
     */
    public static <T extends ConfigOptionListEntry<T>> T findValueByName(String name, List<T> values)
    {
        for (T val : values)
        {
            if (val.getStringValue().equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return values.get(0);
    }

    public static <T extends BaseConfigOptionListEntry<T>> ImmutableList<T> initValues(ImmutableList<T> values)
    {
        for (T val : values)
        {
            val.init(values);
        }

        return values;
    }
}
