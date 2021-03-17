package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.util.JsonConfigUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class JsonModConfig extends BaseModConfig
{
    public JsonModConfig(ModInfo modInfo, int configVersion, List<ConfigOptionCategory> configOptionCategories)
    {
        this(modInfo, modInfo.getModId() + ".json", configVersion, configOptionCategories);
    }

    public JsonModConfig(ModInfo modInfo, String configFileName, int configVersion,
                         List<ConfigOptionCategory> configOptionCategories)
    {
        super(modInfo, configFileName, configVersion, configOptionCategories);
    }

    @Override
    public void loadFromFile(File configFile)
    {
        JsonConfigUtils.loadFromFile(configFile, this.getConfigOptionCategories());
    }

    @Override
    public boolean saveToFile(File configDirectory, File configFile)
    {
        if (this.backupCount > 0)
        {
            FileUtils.createRollingBackup(configFile, this.getConfigBackupDirectory(configDirectory),
                                          this.backupCount, ".bak_");
        }

        return JsonConfigUtils.saveToFile(configFile, this.getConfigOptionCategories(), this.getConfigVersion());
    }
}
