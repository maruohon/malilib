package fi.dy.masa.malilib.config.option.list;

import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;

public class BlackWhiteListConfig<TYPE> extends BaseGenericConfig<BlackWhiteList<TYPE>>
{
    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public BlackWhiteListConfig(String name, BlackWhiteList<TYPE> defaultValue, String prettyName, String comment)
    {
        super(name, defaultValue, name, prettyName, comment);
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

                    ValueListConfig<TYPE> blackList = this.value.getBlackList().copy();
                    ValueListConfig<TYPE> whiteList = this.value.getWhiteList().copy();

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
