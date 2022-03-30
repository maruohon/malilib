package fi.dy.masa.malilib.config.category;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.malilib.util.data.NameIdentifiable;

public interface ConfigOptionCategory extends NameIdentifiable
{
    /**
     * @return The ModInfo of the mod that this config category belongs to
     */
    ModInfo getModInfo();

    /**
     * @return Whether the configs in this category should be saved to file
     */
    boolean shouldSaveToFile();

    /**
     * @return The list of config options in this category.
     * This list is used in the config saving and loading methods,
     * and also for checking if some values are dirty and the config
     * should be saved to file again.
     */
    List<? extends ConfigOption<?>> getConfigOptions();

    /**
     * Resets all the contained config options to their default values
     */
    default void resetAllOptionsToDefaults()
    {
        for (ConfigOption<?> config : this.getConfigOptions())
        {
            config.resetToDefault();
        }
    }
}
