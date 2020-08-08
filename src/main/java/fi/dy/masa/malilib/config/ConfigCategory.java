package fi.dy.masa.malilib.config;

public interface ConfigCategory
{
    /**
     * Returns the internal (config-savable) name of this category
     * @return
     */
    String getName();

    /**
     * Returns the display name for this config category/tab
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
