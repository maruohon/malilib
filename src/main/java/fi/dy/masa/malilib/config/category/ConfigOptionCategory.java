package fi.dy.masa.malilib.config.category;

import java.util.List;
import fi.dy.masa.malilib.config.ConfigDeserializer;
import fi.dy.masa.malilib.config.ConfigOption;
import fi.dy.masa.malilib.config.ConfigSerializer;

public interface ConfigOptionCategory extends ConfigCategory
{
    /**
     * Returns whether or not the configs in this category should be saved to file
     * @return
     */
    boolean shouldSaveToFile();

    /**
     * Returns the list of config options in this category.
     * This list is used in the config saving and loading methods,
     * and also for checking if some values are dirty and the config
     * should be saved to file again.
     * @return
     */
    List<? extends ConfigOption<?>> getConfigOptions();

    /**
     * Returns the config de-serializer that should be used for the configs in this category
     * @return
     */
    ConfigDeserializer getDeserializer();

    /**
     * Returns the config serializer that should be used for the configs in this category
     * @return
     */
    ConfigSerializer getSerializer();
}
