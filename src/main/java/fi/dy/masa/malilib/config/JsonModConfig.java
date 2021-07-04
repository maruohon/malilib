package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.util.JsonConfigUtils;
import fi.dy.masa.malilib.util.BackupUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class JsonModConfig extends BaseModConfig
{
    @Nullable protected ConfigDataUpdater configDataUpdater;

    public JsonModConfig(ModInfo modInfo, int configVersion, List<ConfigOptionCategory> configOptionCategories)
    {
        this(modInfo, modInfo.getModId() + ".json", configVersion, configOptionCategories);
    }

    public JsonModConfig(ModInfo modInfo, String configFileName, int configVersion,
                         List<ConfigOptionCategory> configOptionCategories)
    {
        super(modInfo, configFileName, configVersion, configOptionCategories);
    }

    public JsonModConfig(ModInfo modInfo, String configFileName, int configVersion,
                         List<ConfigOptionCategory> configOptionCategories, @Nullable ConfigDataUpdater configDataUpdater)
    {
        super(modInfo, configFileName, configVersion, configOptionCategories);

        this.configDataUpdater = configDataUpdater;
    }

    public JsonModConfig setConfigDataUpdater(@Nullable ConfigDataUpdater configDataUpdater)
    {
        this.configDataUpdater = configDataUpdater;
        return this;
    }

    protected void updateConfigDataBeforeLoading(int configVersion, JsonObject root)
    {
        this.savedConfigVersion = configVersion;

        if (this.configDataUpdater != null)
        {
            this.configDataUpdater.updateConfigData(root, configVersion);
        }
    }

    @Override
    public void loadFromFile(File configFile)
    {
        JsonConfigUtils.loadFromFile(configFile, this.getConfigOptionCategories(), this::updateConfigDataBeforeLoading);
    }

    @Override
    public boolean saveToFile(File configDirectory, File configFile)
    {
        File backupDirectory = this.getConfigBackupDirectory(configDirectory);
        int currentConfigVersion = this.getConfigVersion();

        if (this.savedConfigVersion != currentConfigVersion)
        {
            BackupUtils.createBackupFileForVersion(configFile, backupDirectory, this.savedConfigVersion);
        }

        if (this.backupCount > 0)
        {
            BackupUtils.createRollingBackup(configFile, backupDirectory, ".bak_", this.backupCount, this.antiDuplicate);
        }

        boolean success = JsonConfigUtils.saveToFile(configFile, this.getConfigOptionCategories(), currentConfigVersion);

        if (success)
        {
            this.savedConfigVersion = currentConfigVersion;
        }

        return success;
    }

    public interface ConfigDataUpdater
    {
        /**
         * Upgrades or modifies the config data before loading
         * @param root the root JsonObject that was read from the config file
         * @param configDataVersion the config version that was read from file. This will be 0 if the file did not yet use a version number.
         */
        void updateConfigData(JsonObject root, int configDataVersion);
    }
}
