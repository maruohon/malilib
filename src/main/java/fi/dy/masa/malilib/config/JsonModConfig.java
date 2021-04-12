package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import com.google.gson.JsonObject;
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

    protected void upgradeConfigDataBeforeLoading(int configVersion, JsonObject obj)
    {
        this.savedConfigVersion = configVersion;
    }

    @Override
    public void loadFromFile(File configFile)
    {
        JsonConfigUtils.loadFromFile(configFile, this.getConfigOptionCategories(), this::upgradeConfigDataBeforeLoading);
    }

    @Override
    public boolean saveToFile(File configDirectory, File configFile)
    {
        File backupDirectory = this.getConfigBackupDirectory(configDirectory);
        int currentConfigVersion = this.getConfigVersion();

        if (this.savedConfigVersion != currentConfigVersion)
        {
            FileUtils.createBackupFileForVersion(configFile, backupDirectory, this.savedConfigVersion);
        }

        if (this.backupCount > 0)
        {
            FileUtils.createRollingBackup(configFile, backupDirectory, this.backupCount, ".bak_");
        }

        boolean success = JsonConfigUtils.saveToFile(configFile, this.getConfigOptionCategories(), currentConfigVersion);

        if (success)
        {
            this.savedConfigVersion = currentConfigVersion;
        }

        return success;
    }
}
