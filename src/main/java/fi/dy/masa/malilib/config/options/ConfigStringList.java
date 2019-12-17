package fi.dy.masa.malilib.config.options;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigStringList extends ConfigBase<ConfigStringList> implements IConfigStringList, IConfigSavable
{
    private final ImmutableList<String> defaultValue;
    private final List<String> strings = new ArrayList<>();
    private final List<String> lastSavedStrings = new ArrayList<>();

    public ConfigStringList(String name, ImmutableList<String> defaultValue, String comment)
    {
        super(ConfigType.STRING_LIST, name, comment);

        this.defaultValue = defaultValue;
        this.strings.addAll(defaultValue);

        this.cacheSavedValue();
    }

    @Override
    public List<String> getStrings()
    {
        return this.strings;
    }

    @Override
    public ImmutableList<String> getDefaultStrings()
    {
        return this.defaultValue;
    }

    @Override
    public void setStrings(List<String> strings)
    {
        List<String> oldStrings = new ArrayList<>();
        oldStrings.addAll(this.strings);

        this.strings.clear();
        this.strings.addAll(strings);

        if (oldStrings.equals(this.strings) == false)
        {
            this.onValueChanged();
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
        this.lastSavedStrings.clear();
        this.lastSavedStrings.addAll(this.strings);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        this.strings.clear();

        try
        {
            if (element.isJsonArray())
            {
                JsonArray arr = element.getAsJsonArray();
                final int count = arr.size();

                for (int i = 0; i < count; ++i)
                {
                    this.strings.add(arr.get(i).getAsString());
                }
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
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
