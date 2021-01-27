package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.util.JsonUtils;

public class JsonModConfig extends BaseModConfig
{
    public JsonModConfig(String modId, String modName, List<ConfigOptionCategory> configOptionCategories, int configVersion)
    {
        super(modId, modName, modId + ".json", configOptionCategories, configVersion);
    }

    @Override
    public void loadFromFile(File configFile)
    {
        JsonElement element = JsonUtils.parseJsonFile(configFile);

        if (element != null && element.isJsonObject())
        {
            JsonObject root = element.getAsJsonObject();
            int configVersion = JsonUtils.getIntegerOrDefault(root, "config_version", -1);

            for (ConfigOptionCategory category : this.getConfigOptionCategories())
            {
                ConfigUtils.readConfig(root, category, configVersion);
            }
        }
    }

    @Override
    public boolean saveToFile(File configFile)
    {
        JsonObject root = new JsonObject();
        root.add("config_version", new JsonPrimitive(this.getConfigVersion()));

        for (ConfigOptionCategory category : this.getConfigOptionCategories())
        {
            if (category.shouldSaveToFile())
            {
                ConfigUtils.writeConfig(root, category.getName(), category.getConfigOptions(), category.getSerializer());
            }
        }

        return JsonUtils.writeJsonToFile(root, configFile);
    }
}
