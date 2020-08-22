package fi.dy.masa.malilib.config.option;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;

public class BlackWhiteListConfig extends BaseConfig<BlackWhiteList>
{
    protected final BlackWhiteList defaultValue;
    protected BlackWhiteList value;
    protected BlackWhiteList lastSavedValue;

    public BlackWhiteListConfig(String name, BlackWhiteList defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList defaultValue, String prettyName, String comment)
    {
        super(name, name, prettyName, comment);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
    }

    public BlackWhiteList getValue()
    {
        return this.value;
    }

    public BlackWhiteList getDefaultValue()
    {
        return this.defaultValue;
    }

    public void setValue(BlackWhiteList value)
    {
        BlackWhiteList oldValue = this.value;

        if (oldValue.equals(value) == false)
        {
            this.value = value;
            this.onValueChanged(value, oldValue);
        }
    }

    @Override
    public boolean isModified()
    {
        return this.value.equals(this.defaultValue) == false;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedValue.equals(this.value) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValue = this.value;
    }

    @Override
    public void resetToDefault()
    {
        this.setValue(this.defaultValue);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasString(obj, "type") &&
                    JsonUtils.hasArray(obj, "blacklist") &&
                    JsonUtils.hasArray(obj, "whitelist"))
                {
                    UsageRestriction.ListType type = BaseConfigOptionListEntry.findValueByName(JsonUtils.getString(obj, "type"), UsageRestriction.ListType.VALUES);
                    ImmutableList<String> blackList = ImmutableList.copyOf(JsonUtils.arrayAsStringList(obj.getAsJsonArray("blacklist")));
                    ImmutableList<String> whiteList = ImmutableList.copyOf(JsonUtils.arrayAsStringList(obj.getAsJsonArray("whitelist")));
                    this.value = new BlackWhiteList(type, blackList, whiteList);
                    this.onValueLoaded(this.value);
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();

        obj.add("type", new JsonPrimitive(this.value.getListType().getStringValue()));
        obj.add("blacklist", JsonUtils.stringListAsArray(this.value.getBlackList()));
        obj.add("whitelist", JsonUtils.stringListAsArray(this.value.getWhiteList()));

        return obj;
    }
}
