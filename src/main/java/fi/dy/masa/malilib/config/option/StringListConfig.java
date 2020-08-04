package fi.dy.masa.malilib.config.option;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;

public class StringListConfig extends BaseConfig<ImmutableList<String>>
{
    protected final ImmutableList<String> defaultValue;
    protected ImmutableList<String> strings;
    protected ImmutableList<String> lastSavedStrings;

    public StringListConfig(String name, ImmutableList<String> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringListConfig(String name, ImmutableList<String> defaultValue, String comment)
    {
        super(name, comment);

        this.defaultValue = defaultValue;
        this.strings = ImmutableList.copyOf(defaultValue);

        this.cacheSavedValue();
    }

    public ImmutableList<String> getStrings()
    {
        return this.strings;
    }

    public ImmutableList<String> getDefaultStrings()
    {
        return this.defaultValue;
    }

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

            this.onValueLoaded(this.strings);
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
