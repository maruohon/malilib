package fi.dy.masa.malilib.config.option;

import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;

public class BlackWhiteListConfig<TYPE, CFG extends ValueListConfig<TYPE>> extends BaseConfig<BlackWhiteList<TYPE, CFG>>
{
    protected final BlackWhiteList<TYPE, CFG> defaultValue;
    protected BlackWhiteList<TYPE, CFG> value;
    protected BlackWhiteList<TYPE, CFG> lastSavedValue;

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE, CFG> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE, CFG> defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE, CFG> defaultValue, String prettyName, String comment)
    {
        super(name, name, prettyName, comment);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
    }

    public BlackWhiteList<TYPE, CFG> getValue()
    {
        return this.value;
    }

    public BlackWhiteList<TYPE, CFG> getDefaultValue()
    {
        return this.defaultValue;
    }

    public void setValue(BlackWhiteList<TYPE, CFG> value)
    {
        BlackWhiteList<TYPE, CFG> oldValue = this.value;

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
        this.lastSavedValue = this.value.copy();
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
                    List<String> blackListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("blacklist"));
                    List<String> whiteListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("whitelist"));

                    @SuppressWarnings("unchecked")
                    CFG blackList = (CFG) this.value.getBlackList().copy();
                    @SuppressWarnings("unchecked")
                    CFG whiteList = (CFG) this.value.getWhiteList().copy();

                    blackList.setValues(ValueListConfig.getStringListAsValues(blackListStr, this.value.getFromStringConverter()));
                    whiteList.setValues(ValueListConfig.getStringListAsValues(whiteListStr, this.value.getFromStringConverter()));

                    this.value = new BlackWhiteList<>(type, blackList, whiteList, this.value.getToStringConverter(), this.value.getFromStringConverter());
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
        obj.add("blacklist", JsonUtils.stringListAsArray(this.value.getBlackListAsString()));
        obj.add("whitelist", JsonUtils.stringListAsArray(this.value.getWhiteListAsString()));

        return obj;
    }
}
