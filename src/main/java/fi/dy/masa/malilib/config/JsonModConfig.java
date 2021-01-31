package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.util.JsonConfigUtils;
import fi.dy.masa.malilib.util.FileUtils;

public class JsonModConfig extends BaseModConfig
{
    public JsonModConfig(String modId, String modName, List<ConfigOptionCategory> configOptionCategories, int configVersion)
    {
        super(modId, modName, modId + ".json", configOptionCategories, configVersion);
    }

    @Override
    public void loadFromFile(File configFile)
    {
        JsonConfigUtils.loadFromFile(configFile, this.getConfigOptionCategories());
    }

    @Override
    public boolean saveToFile(File configFile)
    {
        if (this.backupCount > 0)
        {
            FileUtils.createRollingBackup(configFile, this.backupCount, ".bak_");
        }

        return JsonConfigUtils.saveToFile(configFile, this.getConfigOptionCategories(), this.getConfigVersion());
    }
}
