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

    public JsonModConfig(ModInfo modInfo,
                         int configVersion,
                         List<ConfigOptionCategory> configOptionCategories)
    {
        this(modInfo, modInfo.getModId() + ".json", configVersion, configOptionCategories);
    }

    public JsonModConfig(ModInfo modInfo,
                         String configFileName,
                         int configVersion,
                         List<ConfigOptionCategory> configOptionCategories)
    {
        super(modInfo, configFileName, configVersion, configOptionCategories);
    }

    public JsonModConfig(ModInfo modInfo,
                         String configFileName,
                         int configVersion,
                         List<ConfigOptionCategory> configOptionCategories,
                         @Nullable ConfigDataUpdater configDataUpdater)
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
            this.configDataUpdater.updateConfigDataBeforeLoading(root, configVersion);
        }
    }

    @Override
    public void loadFromFile(File configFile)
    {
        JsonConfigUtils.loadFromFile(configFile, this.getConfigOptionCategories(), this::updateConfigDataBeforeLoading);

        if (this.configDataUpdater != null)
        {
            this.configDataUpdater.updateConfigsAfterLoading(this.getConfigOptionCategories(),
                                                             this.savedConfigVersion, this.getConfigVersion());
        }
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

        int backupCount = this.backupCountSupplier.getAsInt();

        if (backupCount > 0)
        {
            boolean antiDuplicate = this.antiDuplicateSupplier.getAsBoolean();
            BackupUtils.createRollingBackup(configFile, backupDirectory, ".bak_", backupCount, antiDuplicate);
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
         * Updates or modifies the config data before loading
         * @param root the root JsonObject that was read from the config file
         * @param configDataVersion the config version that was read from file.
         *                          This will be 0 if the file did not yet use a version number.
         */
        void updateConfigDataBeforeLoading(JsonObject root, int configDataVersion);

        /**
         * Updates or modifies the configs after they have been read from file.
         * @param categories the config categories belonging to the mod config this updater is running for
         * @param readConfigDataVersion the config data version that was read from the config file
         * @param currentConfigDataVersion the current config data version in the running mod instance
         */
        void updateConfigsAfterLoading(List<ConfigOptionCategory> categories,
                                       int readConfigDataVersion,
                                       int currentConfigDataVersion);
    }

    public static ModConfig createJsonModConfig(ModInfo modInfo, int configVersion,
                                                List<ConfigOptionCategory> configOptionCategories,
                                                ConfigDataUpdater updater)
    {
        JsonModConfig config = new JsonModConfig(modInfo, configVersion, configOptionCategories);
        config.setConfigDataUpdater(updater);
        return config;
    }
}
