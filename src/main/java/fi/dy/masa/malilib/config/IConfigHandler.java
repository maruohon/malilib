package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public interface IConfigHandler
{
    /**
     * Returns the directory where the configs should be saved
     * @return
     */
    default File getConfigDirectory()
    {
        return FileUtils.getConfigDirectory();
    }

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
     * Returns all the configs grouped by their categories
     * @return
     */
    Map<String, List<? extends IConfigBase>> getConfigsPerCategories();

    /**
     * Whether or not the configs in this category should be shown on the config GUIs,
     * or only saved and loaded to/from the config files (internal configs/value storage).
     * @param category
     * @return
     */
    default boolean shouldShowCategoryOnConfigGuis(String category)
    {
        return true;
    }

    /**
     * Whether or not the configs in this category should be saved to file
     * @param category
     * @return
     */
    default boolean shouldSaveCategoryToFile(String category)
    {
        return true;
    }

    /**
     * Returns true if at least some of the config values have changed since last saving to disk.
     * @return
     */
    default boolean areConfigsDirty()
    {
        for (List<? extends IConfigBase> list : this.getConfigsPerCategories().values())
        {
            for (IConfigBase config : list)
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
     */
    default void onConfigsChanged()
    {
        this.saveIfDirty();
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

                for (Map.Entry<String, List<? extends IConfigBase>> entry : this.getConfigsPerCategories().entrySet())
                {
                    ConfigUtils.readConfigBase(root, entry.getKey(), entry.getValue());
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
            MaLiLib.logger.error("Failed to create config directory '{}'", dir.getName());
        }

        if (dir.exists() && dir.isDirectory())
        {
            JsonObject root = new JsonObject();

            for (Map.Entry<String, List<? extends IConfigBase>> entry : this.getConfigsPerCategories().entrySet())
            {
                ConfigUtils.writeConfigBase(root, entry.getKey(), entry.getValue());
            }

            JsonUtils.writeJsonToFile(root, new File(dir, this.getConfigFileName()));
        }
    }

    /**
     * Save the configs only if at least some of them have been modified since last saving
     */
    default void saveIfDirty()
    {
        if (this.areConfigsDirty())
        {
            this.save();
        }
    }
}
