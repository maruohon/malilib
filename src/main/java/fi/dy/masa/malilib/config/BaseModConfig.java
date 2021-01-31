package fi.dy.masa.malilib.config;

import java.util.List;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;

public abstract class BaseModConfig implements ModConfig
{
    protected final String modId;
    protected final String modName;
    protected final String configFileName;
    protected final List<ConfigOptionCategory> configOptionCategories;
    protected final int configVersion;
    protected int backupCount = 5;

    public BaseModConfig(String modId, String modName, String configFileName, List<ConfigOptionCategory> configOptionCategories, int configVersion)
    {
        this.modId = modId;
        this.modName = modName;
        this.configFileName = configFileName;
        this.configOptionCategories = configOptionCategories;
        this.configVersion = configVersion;
    }

    @Override
    public String getModId()
    {
        return this.modId;
    }

    @Override
    public String getModName()
    {
        return this.modName;
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
        return this.configVersion;
    }

    /**
     * Sets the maximum number of backup copies to keep of the config file
     */
    public void setBackupCount(int backupCount)
    {
        this.backupCount = backupCount;
    }
}
