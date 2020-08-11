package fi.dy.masa.malilib.config;

public interface ConfigCategory
{
    /**
     * Returns the internal (config-savable) name of this category.
     * This name is by default used as the JsonObject name in the config file
     * for grouping the options of one category.
     * @return
     */
    String getName();

    /**
     * Returns the mod name this category belongs to.
     * Used on the config screen when showing options from multiple categories or all mods.
     * @return
     */
    String getModName();

    /**
     * Returns the display name for this config category/tab.
     * This is used in the config screen tab buttons and also
     * as the category name for the options when showing options
     * from multiple categories or from all mods.
     * @return
     */
    String getDisplayName();

    /**
     * Returns whether or not this category should appear on the config screen
     * @return
     */
    boolean showOnConfigScreen();

    /**
     * 
     * Whether or not the config GUI tab should include the keybind search button
     * @return
     */
    boolean useKeyBindSearch();
}
