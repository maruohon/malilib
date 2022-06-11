package fi.dy.masa.malilib.config;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class BaseModConfig implements ModConfig
{
    protected final ModInfo modInfo;
    protected final String configFileName;
    protected final List<ConfigOptionCategory> configOptionCategories;
    protected final int currentConfigVersion;
    protected IntSupplier backupCountSupplier = MaLiLibConfigs.Generic.CONFIG_BACKUP_COUNT::getIntegerValue;
    protected BooleanSupplier antiDuplicateSupplier = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE::getBooleanValue;
    protected String backupDirectoryName = "backups";
    protected int savedConfigVersion;

    public BaseModConfig(ModInfo modInfo, String configFileName, int currentConfigVersion,
                         List<ConfigOptionCategory> configOptionCategories)
    {
        this.modInfo = modInfo;
        this.configFileName = configFileName;
        this.configOptionCategories = configOptionCategories;
        this.currentConfigVersion = currentConfigVersion;
    }

    @Override
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public String getConfigFileName()
    {
        return this.configFileName;
    }

    @Override
    public List<ConfigOptionCategory> getConfigOptionCategories()
    {
        return this.configOptionCategories;
    }

    @Override
    public int getConfigVersion()
    {
        return this.currentConfigVersion;
    }

    protected Path getConfigBackupDirectory(Path configDirectory)
    {
        return configDirectory.resolve(this.backupDirectoryName);
    }

    /**
     * Sets the maximum number of backup copies to keep of the config file
     */
    public void setBackupCountSupplier(IntSupplier backupCountSupplier)
    {
        this.backupCountSupplier = backupCountSupplier;
    }

    public void setAntiDuplicateSupplier(BooleanSupplier antiDuplicateSupplier)
    {
        this.antiDuplicateSupplier = antiDuplicateSupplier;
    }

    /**
     * A convenience method to create the default/recommended mod config for the current platform.
     * On 1.13+ Forge this will be TomlModConfig, in all other cases it will be JsonModConfig.
     */
    public static ModConfig createDefaultModConfig(ModInfo modInfo, int configVersion,
                                                   List<ConfigOptionCategory> configOptionCategories)
    {
        return new JsonModConfig(modInfo, configVersion, configOptionCategories);
    }
}
