package fi.dy.masa.malilib.config.category;

public interface ConfigCategory
{
    /**
     * Returns the internal (config-savable) name of this category.
     * This name is by default used as the JsonObject name in the config file
     * for grouping the options of one category.
     * @return
     */
    String getName();
}
