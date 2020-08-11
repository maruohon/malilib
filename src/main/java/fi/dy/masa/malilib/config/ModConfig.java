package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public interface ModConfig
{
    /**
     * Returns the directory where the configs should be saved
     * @return
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
     * Returns the mod ID this handler belongs to
     * @return
     */
    String getModId();

    /**
     * Returns a human-friendly mod name owning the configs of this handler.
     * This is used in things like the hotkey info toast/popup for
     * showing which which hotkey from which mod was triggered.
     * @return
     */
    String getModName();

    /**
     * Returns the filename for the configs
     * @return
     */
    String getConfigFileName();

    /**
     * Returns all the configs in this mod grouped by their categories
     * @return
     */
    List<ConfigOptionCategory> getConfigOptionCategories();

    /**
     * Returns true if at least some of the config values have changed since last saving to disk.
     * @return
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
    default void load()
    {
        File configFile = new File(this.getConfigDirectory(), this.getConfigFileName());

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                for (ConfigOptionCategory category : this.getConfigOptionCategories())
                {
                    ConfigUtils.readConfigBase(root, category.getName(), category.getConfigOptions());
                }
            }
        }

        this.onPostLoad();
    }

    /**
     * Called after the load() method has loaded the configs, to allow
     * mods to do some custom setup with the new config options.
     */
    default void onPostLoad()
    {
    }

    /**
     * Called to unconditionally save all configs to a file
     */
    default void save()
    {
        File dir = this.getConfigDirectory();

        if (dir.exists() == false && dir.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("Failed to create config directory '{}'", dir.getName());
        }

        if (dir.exists() && dir.isDirectory())
        {
            JsonObject root = new JsonObject();

            for (ConfigOptionCategory category : this.getConfigOptionCategories())
            {
                ConfigUtils.writeConfigBase(root, category.getName(), category.getConfigOptions());
            }

            JsonUtils.writeJsonToFile(root, new File(dir, this.getConfigFileName()));
        }
    }

    /**
     * Save the configs only if at least some of them have been modified since last saving
     */
    default boolean saveIfDirty()
    {
        if (this.areConfigsDirty())
        {
            this.save();
            return true;
        }

        return false;
    }
}
