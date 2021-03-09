package fi.dy.masa.malilib.config.value;

import java.util.List;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseOptionListConfigValue implements OptionListConfigValue
{
    protected final String name;
    protected final String translationKey;

    public BaseOptionListConfigValue(String name, String translationKey)
    {
        this.name = name;
        this.translationKey = translationKey;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    /**
     * 
     * Finds the value by the given name from the provided list.
     * If none of the entries match, then the first entry is returned as a fall-back.
     * @param name
     * @param values
     * @return
     */
    public static <T extends OptionListConfigValue> T findValueByName(String name, List<T> values)
    {
        for (T val : values)
        {
            if (val.getName().equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return values.get(0);
    }
}
