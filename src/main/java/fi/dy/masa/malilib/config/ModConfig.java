package fi.dy.masa.malilib.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.util.ConfigUtils;
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
    void loadFromFile(Path configFile);

    /**
     * Saves all the configs to the provided config file
     * @param configDirectory the directory where the configs are being saved to
     * @param configFile the file to save the configs to
     * @return true on success, false on failure
     */
    boolean saveToFile(Path configDirectory, Path configFile);

    /**
     * Returns the directory where the configs should be saved
     */
    default Path getConfigDirectory()
    {
        Path dir = ConfigUtils.getActiveConfigDirectory();
        FileUtils.createDirectoriesIfMissing(dir);
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
        Path configFile = this.getConfigDirectory().resolve(this.getConfigFileName());

        if (Files.isReadable(configFile))
        {
            this.loadFromFile(configFile);
        }
        else
        {
            MaLiLib.LOGGER.warn("ModConfig#loadFromFile(): File '{}' is not readable", configFile.toAbsolutePath());
        }

        this.onPostLoad();
    }

    /**
     * Called after the {@link #loadFromFile(Path)} method has loaded the configs, to allow
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
        Path dir = this.getConfigDirectory();

        if (Files.isDirectory(dir))
        {
            return this.saveToFile(dir, dir.resolve(this.getConfigFileName()));
        }
        else
        {
            MaLiLib.LOGGER.warn("ModConfig#saveToFile(): '{}' is not a valid directory", dir.toAbsolutePath());
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
