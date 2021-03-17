package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface ModConfig
{
    /**
     * Returns the ModInfo of the mod this handler belongs to
     */
    ModInfo getModInfo();

    /**
     * Returns the filename for the configs
     */
    String getConfigFileName();

    /**
     * Returns all the configs in this mod grouped by their categories.
     * This method is used for the config file saving and loading related operations.
     * <br>
     * These categories can be different than what is shown
     * on the config screens, as the config screen tabs are defined separately.
     */
    List<ConfigOptionCategory> getConfigOptionCategories();

    /**
     * Returns the current version number of the configs.
     * This can be used to adjust or reset some values when loading configs
     * from file that were last saved in some older version of the config scheme,
     * or if some configs used to have bad default values etc.
     * @return the current config scheme version
     */
    int getConfigVersion();

    /**
     * Reads all the configs from the provided config file.
     * @param configFile the file to load the configs from
     */
    void loadFromFile(File configFile);

    /**
     * Saves all the configs to the provided config file
     * @param configDirectory the directory where the configs are being saved to
     * @param configFile the file to save the configs to
     * @return true on success, false on failure
     */
    boolean saveToFile(File configDirectory, File configFile);

    /**
     * Returns the directory where the configs should be saved
     */
    default File getConfigDirectory()
    {
        File dir = FileUtils.getConfigDirectory();

        if (dir.exists() == false && dir.mkdirs() == false)
        {
            MaLiLib.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
        }

        return dir;
    }

    /**
     * Returns true if at least some of the config values have changed since last saving to disk.
     */
    default boolean areConfigsDirty()
    {
        for (ConfigOptionCategory category : this.getConfigOptionCategories())
        {
            for (ConfigOption<?> config : category.getConfigOptions())
            {
                if (config.isDirty())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Called when some settings have (potentially) been changed via some of the config GUIs
     * @return true if some setting were dirty
     */
    default boolean onConfigsPotentiallyChanged()
    {
        return this.saveIfDirty();
    }

    /**
     * Called to (re-)load all the configs from file
     */
    default void loadFromFile()
    {
        File configFile = new File(this.getConfigDirectory(), this.getConfigFileName());

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            this.loadFromFile(configFile);
        }

        this.onPostLoad();
    }

    /**
     * Called after the {@link #loadFromFile(File)} method has loaded the configs, to allow
     * mods to do some custom setup with the new config options.
     */
    default void onPostLoad()
    {
    }

     /**
     * Called to unconditionally save all the configs to a file
     */
    default boolean saveToFile()
    {
        File dir = this.getConfigDirectory();

        if (dir.exists() == false && dir.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("Failed to create config directory '{}'", dir.getName());
        }

        if (dir.exists() && dir.isDirectory())
        {
            return this.saveToFile(dir, new File(dir, this.getConfigFileName()));
        }

        return false;
    }

    /**
     * Save the configs only if at least some of them have been modified since last saving
     */
    default boolean saveIfDirty()
    {
        if (this.areConfigsDirty())
        {
            return this.saveToFile();
        }

        return false;
    }
}
