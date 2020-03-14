package fi.dy.masa.malilib.config.options;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigStringList extends ConfigBase<ImmutableList<String>> implements IConfigStringList, IConfigSavable
{
    private final ImmutableList<String> defaultValue;
    private ImmutableList<String> strings;
    private ImmutableList<String> lastSavedStrings;

    public ConfigStringList(String name, ImmutableList<String> defaultValue, String comment)
    {
        super(ConfigType.STRING_LIST, name, comment);

        this.defaultValue = defaultValue;
        this.strings = ImmutableList.copyOf(defaultValue);

        this.cacheSavedValue();
    }

    @Override
    public ImmutableList<String> getStrings()
    {
        return this.strings;
    }

    @Override
    public ImmutableList<String> getDefaultStrings()
    {
        return this.defaultValue;
    }

    @Override
    public void setStrings(List<String> newStrings)
    {
        if (this.strings.equals(newStrings) == false)
        {
            ImmutableList<String> oldStrings = this.strings;
            this.strings = ImmutableList.copyOf(newStrings);
            this.onValueChanged(this.strings, oldStrings);
        }
    }

    @Override
    public void resetToDefault()
    {
        this.setStrings(this.defaultValue);
    }

    @Override
    public boolean isModified()
    {
        return this.strings.equals(this.defaultValue) == false;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedStrings.equals(this.strings) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedStrings = this.strings;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonArray())
            {
                ImmutableList.Builder<String> builder = ImmutableList.builder();
                JsonArray arr = element.getAsJsonArray();
                final int count = arr.size();

                for (int i = 0; i < count; ++i)
                {
                    builder.add(arr.get(i).getAsString());
                }

                this.strings = builder.build();
            }
            else
            {
                // Make sure to clear the old value in any case
                this.strings = ImmutableList.of();
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            // Make sure to clear the old value in any case
            this.strings = ImmutableList.of();
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonArray arr = new JsonArray();

        for (String str : this.strings)
        {
            arr.add(new JsonPrimitive(str));
        }

        return arr;
    }
}
