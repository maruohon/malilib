package fi.dy.masa.malilib.config;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigOption;

public interface ConfigOptionCategory extends ConfigCategory
{
    /**
     * Returns whether or not the configs in this category should be saved to file
     * @return
     */
    boolean shouldSaveToFile();

    /**
     * Returns the list of config options to display on this tab/in this category
     * @return
     */
    List<? extends ConfigOption<?>> getConfigOptions();
}
