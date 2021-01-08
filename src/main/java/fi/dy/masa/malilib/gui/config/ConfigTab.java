package fi.dy.masa.malilib.gui.config;

import java.util.List;
import fi.dy.masa.malilib.config.category.ConfigCategory;
import fi.dy.masa.malilib.config.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;

public interface ConfigTab extends ConfigCategory
{
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
     * Returns the width of the config options on the config screen
     * @return
     */
    int getConfigWidth();

    /**
     * Returns the button action listener that should be used for this tab's selection button
     * @return
     */
    ButtonActionListener getButtonActionListener(BaseConfigScreen gui);

    /**
     * Returns the list of config options to display on this tab/in this category
     * on the config screens.
     * @return
     */
    List<? extends ConfigInfo> getConfigsForDisplay();

    /**
     * Returns the tab by the given name from the provided list
     * @param tabName
     * @param list
     * @param defaultTab the default value to return, if no matches are found in the provided list
     * @return the first found tab by the given name, or the provided default tab if there were no matches
     */
    static ConfigTab getTabByNameOrDefault(String tabName, List<ConfigTab> list, ConfigTab defaultTab)
    {
        for (ConfigTab tab : list)
        {
            if (tabName.equalsIgnoreCase(tab.getName()))
            {
                return tab;
            }
        }

        return defaultTab;
    }
}
